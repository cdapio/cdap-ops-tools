===================
CDAP Nagios Plugins
===================

Plugins for monitoring CDAP from `Nagios <https://www.nagios.org>`__

Check CDAP Program Plugin
=========================

Nagios-style plugin to check status of CDAP Programs

Requirements::

  curl

Usage::

  check_cdap_program [-hvk] [-t timeout] -u <uri> [-n <namespace>] [T <token>]
         [-f <app.flow>[,<app.flow> ...]]
         [-m <app.mapreduce>[,<app.mapreduce> ...]]
         [-s <app.service>[,<app.service> ...]]
         [-S <app.spark>[,<app.spark> ...]]
         [-w <app.workflow>[,<app.workflow> ...]]
         [-W <app.worker>[,<app.worker> ...]]

Options::

  -h                    Usage information.
  -u <uri>              CDAP Router endpoint to check. Defaults to the
                        environment variable CHECK_CDAP_URI, else empty.
  -n <namespace>        CDAP Namespace to query. Defaults to the environment
                        variable CHECK_CDAP_NAMESPACE, else 'default'.
  -f <app.flow>         CDAP target Flow to check. Each Flow must be prepended
                        with the Application name and delimited with a period.
                        Multiple Application.Flow pairs can be specified as a
                        comma-separated list. Defaults to the environment
                        variable CHECK_CDAP_FLOWS, else empty.
  -m <app.mapreduce>    CDAP target MapReduce to check. Each MapReduce must be
                        prepended with the Application name and delimited with
                        a period. Multiple Application.MapReduce pairs can be
                        specified as a comma-separated list. Defaults to the
                        environment variable CHECK_CDAP_MAPREDUCES, else empty.
  -s <app.service>      CDAP target Service to check. Each Service must be
                        prepended with the Application name and delimited with
                        a period. Multiple Application.Service pairs can be
                        specified as a comma-separated list. Defaults to the
                        environment variable CHECK_CDAP_SERVICES, else empty.
  -S <app.spark>        CDAP target Spark jobs to check. Each Spark job must be
                        prepended with the Application name and delimited with
                        a period. Multiple Application.Spark pairs can be
                        specified as a comma-separated list. Defaults to the
                        environment variable CHECK_CDAP_SPARKS, else empty.
  -w <app.workflow>     CDAP target Workflow to check. Each Workflow must be
                        prepended with the Application name and delimited with
                        a period. Multiple Application.Workflow pairs can be
                        specified as a comma-separated list. Defaults to the
                        environment variable CHECK_CDAP_WORKFLOWS, else empty.
  -W <app.worker>       CDAP target Workers to check. Each Worker must be
                        prepended with the Application name and delimited with
                        a period. Multiple Application.Worker pairs can be
                        specified as a comma-separated list. Defaults to the
                        environment variable CHECK_CDAP_WORKERS, else empty.
  -t <timeout>          Override default timeout (seconds). Defaults to the
                        environment variable CHECK_CDAP_TIMEOUT, else 30.
  -T <token>            CDAP Access Token. Defaults to the environment variable
                        CHECK_CDAP_TOKEN, else empty.
  -k                    Disable SSL certification validation
  -v                    Verbose (debug) output.


Examples::

  Check that a Flow and two Services are running in a specific namespace:
    check_cdap_program -u http://my.cdap.router.endpoint:10000 -n mynamespace -f MyApp.MyFlow \\
    -s MyApp.MyService1,MyApp.MyService2

  Check that a flow is running, using environment variables:
    CHECK_CDAP_URI=http://my.cdap.router.endpoint:10000 \\
    CHECK_CDAP_FLOWS=MyApp.MyFlow \\
    check_cdap_program

