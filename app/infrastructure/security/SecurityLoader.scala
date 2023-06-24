package infrastructure.security

import java.security.Security

import play.api.ApplicationLoader
import play.api.inject.guice.{ GuiceApplicationBuilder, GuiceApplicationLoader }

class SecurityLoader extends GuiceApplicationLoader {
  override protected def builder(context: ApplicationLoader.Context): GuiceApplicationBuilder = {
    Security.setProperty("networkaddress.cache.ttl", "1")
    super.builder(context)
  }
}
