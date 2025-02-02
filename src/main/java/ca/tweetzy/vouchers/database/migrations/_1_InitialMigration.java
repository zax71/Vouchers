/*
 * Vouchers
 * Copyright 2022 Kiran Hart
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ca.tweetzy.vouchers.database.migrations;

import ca.tweetzy.feather.database.DataMigration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class _1_InitialMigration extends DataMigration {

	public _1_InitialMigration() {
		super(1);
	}

	@Override
	public void migrate(Connection connection, String tablePrefix) throws SQLException {
		try (Statement statement = connection.createStatement()) {
			statement.execute("CREATE TABLE " + tablePrefix + "voucher (" +
					"id VARCHAR(64) PRIMARY KEY, " +
					"name TEXT NOT NULL, " +
					"description TEXT NOT NULL, " +
					"reward_mode VARCHAR(32) NOT NULL, " +
					"item TEXT NOT NULL, " +
					"options TEXT NOT NULL, " +
					"rewards TEXT NOT NULL " +
					")");

			statement.execute("CREATE TABLE " + tablePrefix + "voucher_redeem (" +
					"id VARCHAR(36) PRIMARY KEY, " +
					"user VARCHAR(36) NOT NULL, " +
					"voucher VARCHAR(64) NOT NULL, " +
					"time LONG NOT NULL " +
					")");
		}
	}
}
