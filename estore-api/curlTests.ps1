# GET /courses/:id
curl.exe --location --request GET 'localhost:8080/courses/9'

# DELETE /courses/:id
curl.exe --location --request DELETE 'localhost:8080/courses/9'

# POST /courses
curl.exe --location -g --request POST 'localhost:8080/courses/' `
--header 'Accept: application/json' `
--header 'Content-Type: application/json' `
--data-raw '{
  \"course\": {
    \"id\": -1,
    \"title\": \"Demo Course 1\",
    \"price\": 9.99,
    \"description\": \"A demo course\",
    \"studentsEnrolled\": 0,
    \"tags\": [
      \"dolor magna Lorem incididunt\",
      \"cupidatat pariatur ipsum Duis tempor\"
    ],
    \"content\": [
      {
        \"title\": \"aliqua Duis\",
        \"video\": \"ex velit officia in\"
      },
      {
        \"title\": \"sed\",
        \"video\": \"dolor minim aut\"
      }
    ]
  },
  \"userName\": \"admin\"
}'

# GET /users/:userName
curl.exe --location --request GET 'localhost:8080/users/Bob'

# GET /users/:userName/cart
curl.exe --location --request GET 'localhost:8080/users/Bob/cart'

# PUT /users/users
curl.exe --location --request PUT 'localhost:8080/users/' `
--header 'Content-Type: application/json' `
--data-raw '{
  \"userName\": \"Bob\",
  \"courses\": [
    4,
    5,
    6
  ],
  \"shoppingCart\": [
    3,
    7,
    10
  ]
}'

# POST /users/login
curl.exe --location --request POST 'localhost:8080/users/login' `
--header 'Content-Type: text/plain' `
--data-raw 'Bob'

# POST /users/register
curl.exe --location --request POST 'localhost:8080/users/register' `
--header 'Content-Type: application/json' `
--data-raw '{
  \"userName\": \"ipsum qui do\",
  \"registeredCourses\": [
    60250430,
    63873321
  ],
  \"shoppingCart\": [
    42840225,
    45123271
  ]
}'
