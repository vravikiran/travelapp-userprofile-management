package com.localapp.mgmt.userprofile.config;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DebeziumConfig {
	@Value("${user.datasource.host}")
	private String dbHost;
	@Value("${user.datasource.port}")
	private String dbPort;
	@Value("${user.datasource.username}")
	private String userName;
	@Value("${user.datasource.password}")
	private String password;
	@Value("${user.datasource.database}")
	private String database;

	@Bean
	public io.debezium.config.Configuration userConnector() throws IOException {
		File offsetStorageTempFile = new File("offsets_.dat");
		File schemaHistFile = new File("schistory.dat");
		var dbHistoryTempFile = File.createTempFile("dbhistory_", ".dat");
		return io.debezium.config.Configuration.create().with("name", "mysql-connector")
				.with("connector.class", "io.debezium.connector.mysql.MySqlConnector").with("database.hostname", dbHost)
				.with("database.port", dbPort).with("database.user", userName).with("database.password", password)
				.with("database.dbname", database).with("database.allowPublicKeyRetrieval", "true")
				.with("database.server.id", "10181").with("database.server.name", "customer-mysql-db-server")
				.with("database.history", "io.debezium.relational.history.FileDatabaseHistory")
				.with("database.include.list", database)
				.with("schema.history.internal.kafka.bootstrap.servers", "localhost:9092")
				.with("schema.history.internal.kafka.topic", "schema-changes.localapp_user_mgmt")
				.with("database.include.list", database)
				.with("table.include.list",
						"localapp_user_mgmt.travel_agent_profile,localapp_user_mgmt.user_profile,localapp_user_mgmt.role")
				.with("column.include.list",
						"localapp_user_mgmt.user_profile.mobileno_hash,localapp_user_mgmt.user_profile.email_hash,"
								+ "localapp_user_mgmt.user_profile.isactive,localapp_user_mgmt.user_profile.role_id,"
								+ "localapp_user_mgmt.user_profile.created_date,localapp_user_mgmt.user_profile.updated_date,"
								+ "localapp_user_mgmt.travel_agent_profile.mobileno_hash,localapp_user_mgmt.travel_agent_profile.email_hash,"
								+ "localapp_user_mgmt.travel_agent_profile.isactive,localapp_user_mgmt.travel_agent_profile.role_id,"
								+ "localapp_user_mgmt.travel_agent_profile.created_date,localapp_user_mgmt.travel_agent_profile.updated_date")
				.with("snapshot.mode", "initial")
				.with("database.history.file.filename", dbHistoryTempFile.getAbsolutePath())
				.with("offset.storage", "org.apache.kafka.connect.storage.FileOffsetBackingStore")
				.with("offset.storage.file.filename", offsetStorageTempFile.getAbsolutePath())
				.with("offset.flush.interval.ms", "60000")
				.with("value.converter", "org.apache.kafka.connect.json.JsonConverter")
				.with("value.converter.schemas.enable", "false")
				.with("key.converter", "org.apache.kafka.connect.json.JsonConverter")
				.with("key.converter.schemas.enable", "false").with("topic.prefix", "ps_")
				.with("schema.history.internal", "io.debezium.storage.file.history.FileSchemaHistory")
				.with("schema.history.internal.file.filename", schemaHistFile.getAbsolutePath())
				.with("transforms", "unwrap")
				.with("transforms.unwrap.type", "io.debezium.transforms.ExtractNewRecordState")
				.with("transforms.unwrap.add.fields", "op,table").with("time.precision.mode", "connect").build();
	}
}
