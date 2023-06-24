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
  lazy val build: HikariConfig = {
    val hikariConfig = new HikariConfig()

    hikariConfig.setUsername("takapi327")
    //hikariConfig.setPassword("docker")
    hikariConfig.setPassword("takapi327")
    //hikariConfig.setJdbcUrl("jdbc:mysql://127.0.0.1:53306/test")
    //hikariConfig.setJdbcUrl("jdbc:mysql://aurora-cluster-stg.cluster-cqfxqxno4vvs.ap-northeast-1.rds.amazonaws.com/test")
    hikariConfig.setJdbcUrl("jdbc:mariadb://aurora-cluster-stg.cluster-cqfxqxno4vvs.ap-northeast-1.rds.amazonaws.com/test")
    //hikariConfig.setJdbcUrl("jdbc:mysql:aws://aurora-cluster-stg.cluster-cqfxqxno4vvs.ap-northeast-1.rds.amazonaws.com/test")
    //hikariConfig.setDriverClassName("com.mysql.jdbc.Driver")
    hikariConfig.setDriverClassName("org.mariadb.jdbc.Driver")
    //hikariConfig.setDriverClassName("software.aws.rds.jdbc.mysql.Driver")
    hikariConfig.setMaximumPoolSize(5)
    //hikariConfig.setReadOnly(true)
    //hikariConfig.setConnectionTestQuery("SELECT 1")
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
