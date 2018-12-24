/*
 * Copyright (c) 2018, PostgreSQL Global Development Group
 * See the LICENSE file in the project root for more information.
 */

package org.postgresql.udt;

import org.postgresql.core.BaseConnection;
import org.postgresql.core.Oid;
import org.postgresql.jdbc.BooleanTypeUtil;
import org.postgresql.jdbc.PgResultSet;

import java.math.BigDecimal;
import java.sql.SQLException;


// TODO: Review for conversion compatibility with PgResultSet
//       Or best - shared implementation
public class Int2ValueAccess extends BaseValueAccess {

  private final short value;

  public Int2ValueAccess(BaseConnection connection, short value) {
    super(connection);
    this.value = value;
  }

  /**
   * {@inheritDoc}
   *
   * @see Oid#INT2
   */
  @Override
  protected int getOid() {
    return Oid.INT2;
  }

  @Override
  public String getString() throws SQLException {
    return Integer.toString(value);
  }

  /**
   * {@inheritDoc}
   *
   * @see  PgResultSet#getBoolean(int)
   */
  @Override
  public boolean getBoolean() throws SQLException {
    return BooleanTypeUtil.fromNumber(value);
  }

  @Override
  public byte getByte() throws SQLException {
    return (byte)value;
  }

  @Override
  public short getShort() throws SQLException {
    return value;
  }

  @Override
  public int getInt() throws SQLException {
    return value;
  }

  @Override
  public long getLong() throws SQLException {
    return value;
  }

  @Override
  public float getFloat() throws SQLException {
    return value;
  }

  @Override
  public double getDouble() throws SQLException {
    return value;
  }

  @Override
  public BigDecimal getBigDecimal() throws SQLException {
    return BigDecimal.valueOf(value);
  }
}