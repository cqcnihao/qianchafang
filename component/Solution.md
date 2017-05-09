### 分布式配置中心的实现  
实现前提：zookeeper的node可以存储一定大小的数据，主从之前可以  
同步节点上的数据， 所以可以通过node存储需要同步的数据，比如数据  
库的连接信息；可以将url,pw等信息以json形式，保存到一个节点中；  
当集群中的服务器需要连接到数据库时，只需要从node中取出json形式  
的数据，再将其进行一定处理，即可连接到数据库  

### centos7的mysql安装  
centos7使用yum安装时，默认安装的是mariad，安装过程可见[http://www.centoscn.com/mysql/2016/0626/7537.html]()