#Knot.x release procedure
##Prerequisites
- sonatype jira account
- addedd to io.knotx group (can publish to)
- gpg created for the email used in sonatype jira
- settings.xml added ossh credentials (jira)

##Steps
- Set next release version (remove snapshot) versions:set
- release to maven central staging
- validate maven central artifacts
- update what's new
- release documentation, see [Documentation](documentation/Readme.md)
- promote staging artifacts to central repo
- create release on github with (what's new + new maven coordinates)


