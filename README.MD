#  Auth / User-Service

##  Overview
Handles authentication, JWT, OAuth2, and user management.

---

##  Components
- **User**: Stores user details (id, username, email, role, provider, etc.)
- **UserRepository**: DB operations (find, exists, delete)
- **AuthService**: Auth APIs (register, login, token, profile)
- **AuthServiceImpl**: Business logic

---

##  Flow
- **Register** → validate → hash password → save  
- **Login** → verify → generate JWT  
- **OAuth** → fetch user → login/register → JWT  

---

##  Security
- BCrypt password  
- JWT auth  
- Role-based access  

---

##  Tech
Spring Boot • Spring Security • JWT • OAuth2 • MySQL

🔗 **API Base URL:** https://github.com/keshavvyadavv/Code-Collaboration-Platform-Backend/tree/feature/auth-core
