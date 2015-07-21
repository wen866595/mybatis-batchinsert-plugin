# mybatis-batchinsert-plugin
mybatis 支持批量插入的插件。

###   引入插件的配置：mybatis-conf.xml
~~~xml
<?xml version="1.0" encoding="UTF-8" ?>  
    <!DOCTYPE configuration PUBLIC "-//mybatis.org//DTD Config 3.0//EN"  
    "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
	<plugins>
		<plugin interceptor="net.coderbee.mybatis.plugin.BatchInsertParameterHandler" />
		<plugin interceptor="net.coderbee.mybatis.plugin.BatchInsertStatementHandler" />
	</plugins>
</configuration>
~~~


###   调用代码示例

mapper xml :
~~~xml
<insert id="addUserBatch" parameterType="net.coderbee.mybatis.parameter.BatchInsertParameter">
	insert into users (name, age, sex) values (#{name}, #{age}, #{sex})
</insert>
~~~


~~~java
@Test
public void testBatchInsert() {
	User user = new User();
	user.setAge(28);
	user.setSex('M');
	user.setName("coderbee");

	User user2 = new User();
	user2.setAge(29);
	user2.setSex('F');
	user2.setName("femail");

	List<User> list2 = Arrays.asList(user, user2);

	mapper.addUserBatch(BatchInsertParameter.wrap(list2, 10));
	System.out.println("ok");
}
~~~

