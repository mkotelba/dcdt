import ch.qos.logback.core.helpers.NOPAppender
import ch.qos.logback.core.status.NopStatusListener
import org.springframework.core.env.PropertySourcesPropertyResolver

/*====================================================================================================
= STATUS LISTENERS
=====================================================================================================*/
statusListener(NopStatusListener)

/*====================================================================================================
= APPENDER: NOP
=====================================================================================================*/
appender("nop", NOPAppender)

/*====================================================================================================
= LOGGERS: SPRING FRAMEWORK
=====================================================================================================*/
logger(PropertySourcesPropertyResolver.name, WARN, [ "nop" ], false)

/*====================================================================================================
= ROOT LOGGER
=====================================================================================================*/
root(WARN, [ "nop" ])