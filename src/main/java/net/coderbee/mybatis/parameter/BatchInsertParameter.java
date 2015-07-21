package net.coderbee.mybatis.parameter;

import java.util.List;

/**
 * 封装要批量插入实体的类，插件判断 Mapper 的 parameterType 是此类的实例时才走批量插入，
 * 否则按 MyBatis 的默认方式处理。
 * 
 * @param <T>
 *            要插入记录的实体类型
 * 
 * @author <a href="http://coderbee.net">coderbee</a>
 *
 */
public class BatchInsertParameter<T> {
	private static final int DEFAULT_BATCH_SIZE = 100;

	private final List<T> data;
	private final int batchSize;

	private BatchInsertParameter(List<T> data, int batchSize) {
		this.data = data;
		this.batchSize = batchSize;
	}

	public List<T> getData() {
		return data;
	}

	public int getBatchSize() {
		return batchSize;
	}

	public static <T> BatchInsertParameter<T> wrap(List<T> data) {
		return wrap(data, DEFAULT_BATCH_SIZE);
	}

	/**
	 * 
	 * @param data
	 *            不能是空
	 * @param batchSize
	 *            不能小于 10
	 * @return
	 */
	public static <T> BatchInsertParameter<T> wrap(List<T> data, int batchSize) {
		if (data == null || data.isEmpty()) {
			throw new IllegalArgumentException("data must not be empty");
		}

		if (batchSize < 10) {
			throw new IllegalArgumentException("batchSize < 10");
		}

		return new BatchInsertParameter<T>(data, batchSize);
	}
}
