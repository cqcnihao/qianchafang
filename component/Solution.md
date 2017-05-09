### 分布式配置中心的实现  
实现前提：zookeeper的node可以存储一定大小的数据，主从之前可以  
同步节点上的数据， 所以可以通过node存储需要同步的数据，比如数据  
库的连接信息；可以将url,pw等信息以json形式，保存到一个节点中；  
当集群中的服务器需要连接到数据库时，只需要从node中取出json形式  
的数据，再将其进行一定处理，即可连接到数据库  

### centos7的mysql安装  
centos7使用yum安装时，默认安装的是mariad，安装过程可见[http://www.centoscn.com/mysql/2016/0626/7537.html]()

### 实体渲染为json时的问题：  
实体序列化为json，用的是fastjson处理，但是不一定满足这里的业务需要
1，实体中的属性不一定全都需要  
2，实体的属性为自定义类型  
3，实体序的查询问题：如果情况如2中，并且该属性是集合类型，根据某几家  
公司牛逼的做法，是使用mybatis各种连表查，再拼装成json，个人不太喜欢这种机械的面向  
过程的编程思路；  

所以这里先思考下，如何设计实体的序列化框架  

### 请求校验问题  
请求到达访问资源以前，需要对其是否合法，进行校验，  
需要在请求体中加个token参数，并且判定是否能访问资源；  