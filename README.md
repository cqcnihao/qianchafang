# qianchafang  
### 目录结构  
`rootpom` 是总依赖，`component`工程依赖它，而其他所有工程以来`component`  

`component`是service和dao层文件存放目录  

`admin`,`api`分别是对后台和对接口的工程，他们都依赖`componet`；  


### 实体基类设计  
实体的四个基本字段:id,data,status,create_time  

设计基类之前，考虑到数据库以后是否分表分库，要使用集群的话，考虑用zookeeper进行管理  

