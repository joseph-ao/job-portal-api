# Job-Portal API Documentation

Base URL: `http://localhost:8080`

> **Authentication**  
> All endpoints **except** those under `/auth/**` require a JWT in the `Authorization` header:
> ```
> Authorization: Bearer <your-jwt-here>
> ```

---

## 1. Authentication

### 1.1 Register Candidate
`POST /auth/register/candidate`

**Description**  
Create a new candidate account. Returns a JWT for immediate use.

**Request Headers**  
`Content-Type: application/json`

**Body**
```json
{
  "username":   "joe123",
  "password":   "hunter2",
  "firstName":  "Joe",
  "lastName":   "Abou Obeid",
  "email":      "joe@example.com",
  "phone":      "96171234567",
  "resumeUrl":  "https://…/joe.pdf"
}
```

**Responses**

*200 OK*
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9…"
}
```

*400 Bad Request*
Username already taken
```json
"Error: Username is already taken!"
```

### 1.2 Register Recruiter
`POST /auth/register/recruiter`

**Description**  
Create a new recruiter account. Returns a JWT for immediate use.

**Request Headers**
`Content-Type: application/json`

**Body**
```json
{
  "username":     "bobcorp",
  "password":     "secure!",
  "companyName":  "Bob's Recruiting",
  "companyEmail": "hr@bobcorp.com",
  "companyPhone": "555-0202"
}
```

**Responses**

*200 OK*
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9…"
}
```

*400 Bad Request*
Username already taken
```json
"Error: Username is already taken!"
```

### 1.3 Login
`POST /auth/login`

**Description**  
Authenticate an existing user (candidate or recruiter) and return a fresh JWT.

**Request Headers**
`Content-Type: application/json`

**Body**
```json
{
  "username": "joe123",
  "password": "hunter2"
}
```

**Responses**

*200 OK*
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9…"
}
```

*401 Unauthorized*
Invalid credentials
```json
"Error: Invalid credentials"
```

## 2. Candidates
Note: `/auth/register/candidate` does creation; the below are additional CRUD operations.

### 2.1 List all Candidates
`GET /candidates`

**Headers**
```
Authorization: Bearer <token>
```

**Response 200 OK**
```json
[
  {
    "id":       "683f5a2b…",
    "firstName":"Joe",
    "lastName": "Abou Obeid",
    "email":    "joe@example.com",
    "phone":    "96171234567",
    "resumeUrl":"https://…/joe.pdf"
  },
  { … }
]
```

### 2.2 Get Candidate by ID
`GET /candidates/{id}`

**Headers**
```
Authorization: Bearer <token>
```

**Response 200 OK**
```json
{
  "id":       "683f5a2b…",
  "firstName":"Joe",
  "lastName": "Abou Obeid",
  "email":    "joe@example.com",
  "phone":    "96171234567",
  "resumeUrl":"https://…/joe.pdf"
}
```

*404 Not Found*
```json
"The candidate you want isn't found"
```

### 2.3 Update Candidate
`PUT /candidates/{id}`

**Headers**
```
Authorization: Bearer <token>
Content-Type: application/json
```

**Body**
```json
{
  "firstName": "Joseph",
  "lastName":  "Abou Obeid",
  "email":     "joseph@example.com",
  "phone":     "96171234567",
  "resumeUrl": "https://…/joseph.pdf"
}
```

**Responses**

*200 OK* — returns updated candidate

*404 Not Found*
```json
"The candidate you want to update isn't found"
```

### 2.4 Delete Candidate
`DELETE /candidates/{id}`

**Headers**
```
Authorization: Bearer <token>
```

**Responses**

*204 No Content*

*404 Not Found*

## 3. Jobs

### 3.1 List all Jobs
`GET /jobs`

**Headers**
```
Authorization: Bearer <token>
```

**Response 200 OK**
```json
[
  {
    "id":      "6823b457…",
    "title":   "Backend Engineer",
    "company": "Acme Corp",
    "salary":  95000
  },
  { … }
]
```

### 3.2 Get Job by ID
`GET /jobs/{id}`

**Headers**
```
Authorization: Bearer <token>
```

**Response 200 OK**
```json
{
  "id":      "6823b457…",
  "title":   "Backend Engineer",
  "company": "Acme Corp",
  "salary":  95000
}
```

*404 Not Found*

### 3.3 Create Job
`POST /jobs`

**Headers**
```
Authorization: Bearer <token>
Content-Type: application/json
```

**Body**
```json
{
  "title":   "Frontend Engineer",
  "company": "Acme Corp",
  "salary":  90000
}
```

**Response 201 Created**
```json
{
  "id":      "683f6d2c…",
  "title":   "Frontend Engineer",
  "company": "Acme Corp",
  "salary":  90000
}
```

### 3.4 Update Job
`PUT /jobs/{id}`

**Headers**
```
Authorization: Bearer <token>
Content-Type: application/json
```

**Body**
```json
{
  "title":   "Senior Frontend Engineer",
  "company": "Acme Corp",
  "salary":  105000
}
```

**Responses**

*200 OK* — updated job

*404 Not Found*

### 3.5 Delete Job
`DELETE /jobs/{id}`

**Headers**
```
Authorization: Bearer <token>
```

**Responses**

*204 No Content*

*404 Not Found*

## 4. Applications

### 4.1 Apply to a Job
`POST /applications`

**Headers**
```
Authorization: Bearer <token>
Content-Type: application/json
```

**Body**
```json
{
  "candidateId": "683f5a2b…",
  "jobId":       "6823b457…"
}
```

**Response 201 Created**
```json
{
  "id":          "684a7b3d…",
  "candidateId": "683f5a2b…",
  "jobId":       "6823b457…",
  "status":      "APPLIED"
}
```

### 4.2 List Applications by Candidate
`GET /applications/{candidateId}`

**Headers**
```
Authorization: Bearer <token>
```

**Response 200 OK**
```json
[
  {
    "id":          "684a7b3d…",
    "candidateId": "683f5a2b…",
    "jobId":       "6823b457…",
    "status":      "APPLIED"
  },
  { … }
]
```

### 4.3 Withdraw Application
`DELETE /applications/{id}`

**Headers**
```
Authorization: Bearer <token>
```

**Responses**

*204 No Content*

*404 Not Found*
