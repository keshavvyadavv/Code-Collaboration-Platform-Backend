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


---

#  File / Editor-Service

##  Overview
Manages project files, folders, and code editing.

##  Components
- **CodeFile**: Stores file data (id, name, path, content, language, size, etc.)
- **FileRepository**: DB operations (find, count, delete)
- **FileService**: File APIs (CRUD, move, rename, search)
- **FileServiceImpl**: Business logic


##  Features
- Create files & folders  
- Update file content (with user + timestamp)  
- Rename / Move files  
- Soft delete & restore  
- File tree structure  
- Search in project  


##  Flow
- **Create File** → save with path  
- **Edit File** → update content + lastEditedBy  
- **Delete File** → soft delete  
- **Restore File** → recover data  


##  Key Points
- Full file content stored  
- Folder = empty content  
- Soft delete support  
- Tracks last editor  


## Tech
Spring Boot • JPA • MySQL

🔗 **API Base URL:** https://github.com/keshavvyadavv/Code-Collaboration-Platform-Backend/tree/feature/Editor-Service
