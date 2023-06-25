package infrastructure.mysql

import javax.inject.Inject

import scala.concurrent.Future

import com.google.inject.AbstractModule

import com.zaxxer.hikari.{ HikariConfig, HikariDataSource }

import com.typesafe.config._

import play.api.inject.ApplicationLifecycle

class HikariCPModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[HikariDataSource]).toInstance(new HikariDataSource(HikariConfigBuilder.build))
    bind(classOf[HikariDataSourceCloser]).asEagerSingleton()
  }
}

private object HikariConfigBuilder {

  private val config: Config = ConfigFactory.load()

  lazy val build: HikariConfig = {
    val hikariConfig = new HikariConfig()

    val userName        = config.getString("hikari.user_name")
    val password        = config.getString("hikari.password")
    val jdbcUrl         = config.getString("hikari.jdbc_url")
    val driverClassName = config.getString("hikari.driver_class_name")

    hikariConfig.setUsername(userName)
    hikariConfig.setPassword(password)
    hikariConfig.setJdbcUrl(jdbcUrl)
    hikariConfig.setDriverClassName(driverClassName)
    hikariConfig.setMaximumPoolSize(5)
    //hikariConfig.setReadOnly(true)
    //hikariConfig.setConnectionInitSql("SELECT test.validation()")
    //hikariConfig.setConnectionInitSql("SELECT 1")
    //hikariConfig.setMaxLifetime(120000)
    hikariConfig.addDataSourceProperty("useSSL", false)

    hikariConfig.validate()
    hikariConfig
  }
}

private class HikariDataSourceCloser @Inject()(
  dataSource: HikariDataSource,
  appLifecycle: ApplicationLifecycle
) {
  appLifecycle.addStopHook { () =>
    Future.successful(dataSource.close())
  }
}
