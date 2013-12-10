-----------------------------------------------
 Elastic Path Search Server Failover Scripts
-----------------------------------------------

This archive contains the scripts required to enable clustering and failover
of the Elastic Path search server.

It contains three directories:

apache
    This directory contains the sample httpd.conf for a software load balancer.
    *DO NOT* replace the existing httpd.conf on your Apache web server.
    The sample file may contain settings that are not compatible with your
    environment. Review the settings carefully. Only copy the values that you
    need.

master
    This directory contains the files that need to be installed in the Solr 
    home directory on the master server. It contains these subdirectories:

    bin
        This directory contains the custom bash scripts.  Copy this directory 
        into searchserver/WEB-INF/solrHome.
    conf
        This directory contains the configuration settings for the custom bash
        scripts. Copy the scripts.conf file into the 
        searchserver/WEB-INF/solrHome/conf directory.
    logs
        All log messages from the individual scripts will be logged in this
        directory. For example, snapshooter.ep will log to logs/snappuller.log. 
        Copy this directory into searchserver/WEB-INF/solrHome.
    tmp
        This directory is used for temporary storage of data during snapshots.  
        Copy this directory into searchserver/WEB-INF/solrHome. 

slave
    This directory contains the files that need to be installed in the Solr 
    home directory on the slave server. It contains these subdirectories:

    bin
        This directory contains the custom bash scripts that need to be
        installed in the Solr home directory on the slave server.  Copy this 
        directory into searchserver/WEB-INF/solrHome. 
    conf
        This directory contains the configuration settings for the custom bash
        scripts. Copy scripts.conf into searchserver/WEB-INF/solrHome/conf.
    logs
        All log messages from the individual scripts will be logged into this
        directory. For example, snappuller.ep will log to logs/snappuller.log. 
        Copy this directory into searchserver/WEB-INF/solrHome.
    tmp
        This directory is used for temporary storage of data during snapshots. 
        Copy this directory into searchserver/WEB-INF/solrHome.

----

For more information on how to configure search server failover, see the 
following:

Elastic Path documentation site: http://docs.elasticpath.com/

Grep Elastic Path community site: http://grep.elasticpath.com/