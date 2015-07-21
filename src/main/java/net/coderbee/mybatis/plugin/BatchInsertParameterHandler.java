package net.coderbee.mybatis.plugin;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Properties;

import net.coderbee.mybatis.parameter.BatchInsertParameter;
import net.coderbee.mybatis.util.ReflectHelper;

import org.apache.ibatis.executor.parameter.DefaultParameterHandler;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;

/**
 * 用于绑定参数到 {@link java.sql.PreparedStatement} 的拦截器。<br/>
 * <br/>
 * 
 * 拦截参数是 {@link net.coderbee.mybatis.parameter.BatchInsertParameter}
 * 的实例的参数绑定，取出里面的实体列表，分批绑定并执行。
 * 
 * @author <a href="http://coderbee.net">coderbee</a>
 *
 */
@Intercepts({ @Signature(type = ParameterHandler.class, method = "setParameters", args = { PreparedStatement.class }) })
public class BatchInsertParameterHandler implements Interceptor {

	public Object intercept(Invocation invocation) throws Throwable {
		if (invocation.getTarget() instanceof DefaultParameterHandler) {
			DefaultParameterHandler parameterHandler = (DefaultParameterHandler) invocation
					.getTarget();

			MappedStatement mappedStatement = (MappedStatement) ReflectHelper
					.getValueByFieldName(parameterHandler, "mappedStatement");

			Object paramObj = ReflectHelper.getValueByFieldName(
					parameterHandler, "parameterObject");
			if (paramObj instanceof BatchInsertParameter) {

				PreparedStatement ps = (PreparedStatement) invocation.getArgs()[0];
				BoundSql boundSql = (BoundSql) ReflectHelper
						.getValueByFieldName(parameterHandler, "boundSql");

				@SuppressWarnings({ "unchecked", "rawtypes" })
				List<Object> parameterObject = (List) ((BatchInsertParameter) paramObj)
						.getData();

				ps.clearBatch();
				ps.clearParameters();

				@SuppressWarnings("rawtypes")
				int batchSize = ((BatchInsertParameter) paramObj)
						.getBatchSize();
				int i = 0;
				for (Object pobject : parameterObject) {
					DefaultParameterHandler handler = new DefaultParameterHandler(
							mappedStatement, pobject, boundSql);
					handler.setParameters(ps);
					ps.addBatch();
					i += 1;
					if (i % batchSize == 0) {
						ps.executeBatch();
					}
				}
				if (parameterObject.size() % batchSize != 0) {
					ps.executeBatch();
				}

				return i;
			}
		}

		return invocation.proceed();
	}

	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	public void setProperties(Properties properties) {

	}

}
