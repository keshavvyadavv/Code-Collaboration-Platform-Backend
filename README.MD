#  File / Editor-Service

🔗 **API Base URL:** https://github.com/keshavvyadavv/Code-Collaboration-Platform-Backend/tree/feature/Editor-Service

---

##  Overview
Manages project files, folders, and code editing.

---

##  Components
- **CodeFile**: Stores file data (id, name, path, content, language, size, etc.)
- **FileRepository**: DB operations (find, count, delete)
- **FileService**: File APIs (CRUD, move, rename, search)
- **FileServiceImpl**: Business logic

---

##  Features
- Create files & folders  
- Update file content (with user + timestamp)  
- Rename / Move files  
- Soft delete & restore  
- File tree structure  
- Search in project  

---

##  Flow
- **Create File** → save with path  
- **Edit File** → update content + lastEditedBy  
- **Delete File** → soft delete  
- **Restore File** → recover data  

---

## 🛡 Key Points
- Full file content stored  
- Folder = empty content  
- Soft delete support  
- Tracks last editor  

---

## Tech
Spring Boot • JPA • MySQL
