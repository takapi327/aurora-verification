package infrastructure.mysql

import javax.inject.Inject

import scala.concurrent.Future

import com.google.inject.AbstractModule

import com.zaxxer.hikari.{ HikariConfig, HikariDataSource }

import play.api.inject.ApplicationLifecycle

class HikariCPModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[HikariDataSource]).toInstance(new HikariDataSource(HikariConfigBuilder.build))
    bind(classOf[HikariDataSourceCloser]).asEagerSingleton()
  }
}

private object HikariConfigBuilder {
  private val maxCore: Int = Runtime.getRuntime.availableProcessors()
  lazy val build: HikariConfig = {
    val hikariConfig = new HikariConfig()

    hikariConfig.setUsername("takapi327")
    hikariConfig.setPassword("")
    hikariConfig.setJdbcUrl("")
    hikariConfig.setDriverClassName("")
    hikariConfig.setMaximumPoolSize(maxCore * 2)
    hikariConfig.setConnectionTestQuery("SELECT 1")

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
