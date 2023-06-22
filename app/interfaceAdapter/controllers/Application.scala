package interfaceAdapter.controllers

import javax.inject.Inject
import play.api.mvc._

class ApplicationController @Inject()(implicit
  cc: MessagesControllerComponents
) extends AbstractController(cc) {

  /**
   * Health check for AWS ALB
   */
  def healthCheck = Action { Ok("ok") }
}
