package net.coderbee.mybatis.plugin;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Properties;

import net.coderbee.mybatis.parameter.BatchInsertParameter;
import net.coderbee.mybatis.util.ReflectHelper;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.statement.PreparedStatementHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;

/**
 * 拦截参数是 {@link net.coderbee.mybatis.parameter.BatchInsertParameter}
 * 的实例的语句执行，由于批量插入在参数绑定的时候已经执行了，这里不需要再次执行。
 * 
 * @author <a href="http://coderbee.net">coderbee</a>
 *
 */
@Intercepts({ @Signature(type = StatementHandler.class, method = "update", args = { Statement.class }) })
public class BatchInsertStatementHandler implements Interceptor {

	// @Override
	public Object intercept(Invocation invocation) throws Throwable {
		if (invocation.getTarget() instanceof RoutingStatementHandler) {
			RoutingStatementHandler routingStatementHandler = (RoutingStatementHandler) invocation
					.getTarget();

			StatementHandler delegate = (StatementHandler) ReflectHelper
					.getValueByFieldName(routingStatementHandler, "delegate");
			if (delegate instanceof PreparedStatementHandler) {

				PreparedStatementHandler preparedStatementHandler = (PreparedStatementHandler) delegate;
				MappedStatement mappedStatement = (MappedStatement) ReflectHelper
						.getValueByFieldName(preparedStatementHandler,
								"mappedStatement");

				BoundSql boundSql = (BoundSql) ReflectHelper
						.getValueByFieldName(preparedStatementHandler,
								"boundSql");

				Object parameterObject = boundSql.getParameterObject();
				if (parameterObject instanceof BatchInsertParameter) {
					PreparedStatement ps = (PreparedStatement) invocation
							.getArgs()[0];
					int rows = ps.getUpdateCount();
					Executor executor = (Executor) ReflectHelper
							.getValueByFieldName(preparedStatementHandler,
									"executor");

					KeyGenerator keyGenerator = mappedStatement
							.getKeyGenerator();

					keyGenerator.processAfter(executor, mappedStatement, ps,
							parameterObject);
					return rows;
				}
			}
		}

		return invocation.proceed();
	}

	// @Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	// @Override
	public void setProperties(Properties properties) {

	}

}
