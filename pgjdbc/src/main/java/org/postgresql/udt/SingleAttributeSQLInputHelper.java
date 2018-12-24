/*
 * Copyright (c) 2018, PostgreSQL Global Development Group
 * See the LICENSE file in the project root for more information.
 */

package org.postgresql.udt;

import org.postgresql.core.BaseConnection;
import org.postgresql.util.GT;
import org.postgresql.util.PSQLException;
import org.postgresql.util.PSQLState;

import java.sql.SQLData;
import java.sql.SQLException;


/**
 * Helper for implementing {@link SingleAttributeSQLInput}.
 */
public class SingleAttributeSQLInputHelper {

  private SingleAttributeSQLInputHelper() {}

  /**
   * Instantiates a {@link SQLData} and reads it from the given {@link SingleAttributeSQLInput}.
   * At this time, only single attribute {@link SQLData} is supported.
   *
   * @param udtMap the current user-defined data types
   * @param type the backend typename
   * @param sqlDataType the class of the custom type to be returned
   * @param in the field source for the custom type
   * @return an appropriate object; never null.
   * @throws SQLException if something goes wrong
   */
  // TODO: udtMap is unused
  public static SQLData readSQLData(UdtMap udtMap, String type, Class<? extends SQLData> sqlDataType, SingleAttributeSQLInput in) throws SQLException {
    // An extremely simple implementation for scalar values only (not composite types)
    // This is useful for DOMAIN (and possibly ENUM?) values mapping onto SQLData
    try {
      SQLData sqlData = sqlDataType.newInstance();
      sqlData.readSQL(in, type);
      if (!in.getReadDone()) {
        throw new PSQLException(GT.tr(
            "No attributes read by SQLData instance of {0}",
            sqlDataType.getName()), PSQLState.DATA_ERROR);
      }
      return sqlData;
    } catch (InstantiationException e) {
      // Copying SYSTEM_ERROR used for IllegalAccessException in Parser.java
      throw new PSQLException(e.getMessage(), PSQLState.SYSTEM_ERROR, e);
    } catch (IllegalAccessException e) {
      // Copying SYSTEM_ERROR used for IllegalAccessException in Parser.java
      throw new PSQLException(e.getMessage(), PSQLState.SYSTEM_ERROR, e);
    }
  }

  /**
   * Implementation of {@link BaseConnection#getObject(java.lang.String, java.lang.String, byte[])} for custom types
   * once the custom type is known.
   *
   * <p>
   * This is present for type inference implemented by {@code org.postgresql.jdbc.PgResultSet.getObject(..., Class<T> type)}, which is
   * a consequence of the server sending base types for domains.
   * </p>
   *
   * @param <T> the custom type to be returned
   * @param udtMap the current user-defined data types
   * @param type the backend typename
   * @param customType the class of the custom type to be returned
   * @param in the field source for the custom type
   * @return an appropriate object; never null.
   * @throws SQLException if something goes wrong
   *
   * @see  BaseConnection#getObject(java.lang.String, java.lang.String, byte[])
   * @see  org.postgresql.jdbc.PgResultSet#getObject(java.lang.String, java.lang.Class)
   * @see  org.postgresql.jdbc.PgResultSet#getObject(int, java.lang.Class)
   *
   * @see  SingleAttributeSQLInputHelper#readSQLData(org.postgresql.udt.UdtMap, java.lang.String, java.lang.Class, org.postgresql.udt.SingleAttributeSQLInput)
   */
  public static <T> T getObjectCustomType(UdtMap udtMap, String type, Class<? extends T> customType, SingleAttributeSQLInput in) throws SQLException {
    if (SQLData.class.isAssignableFrom(customType)) {
      Class<? extends SQLData> sqlDataType = customType.asSubclass(SQLData.class);
      return customType.cast(SingleAttributeSQLInputHelper.readSQLData(udtMap, type, sqlDataType, in));
    }
    // TODO: Support Struct, too
    // Unexected custom type
    throw new PSQLException(GT.tr("Custom type does not implement SQLData: {0}", customType.getName()),
        PSQLState.NOT_IMPLEMENTED);
  }
}