#!/bin/bash
#
# Pushes GWT poms with "com.google" groupIds to a local (the default) or remote maven repository.
# These poms are relocation poms, and point to the new "org.gwtproject" poms, rather than being
# deployed with any artifacts.
# To push remote, set 2 env variables: GWT_MAVEN_REPO_URL and GWT_MAVEN_REPO_ID
#
# GWT_MAVEN_REPO_ID = a server id in your .m2/settings.xml with remote repo username and password
#
# Sonatype staging repo (promotes to Maven Central)
#   GWT_MAVEN_REPO_URL=https://oss.sonatype.org/service/local/staging/deploy/maven2/
#
# Sonatype Google SNAPSHOTs repo (can only deploy SNAPSHOTs here, and they are immediately public)
#   GWT_MAVEN_REPO_URL=https://oss.sonatype.org/content/repositories/google-snapshots/

pushd $(dirname $0) >/dev/null 2>&1

export pomDir=./google-poms

source lib-gwt.sh

# use GWT_MAVEN_REPO_URL if set else M2_REPO else default location for local repo
localRepoUrl=${M2_REPO:="$HOME/.m2/repository"}
localRepoUrl="file://$localRepoUrl"
repoUrlDefault=${GWT_MAVEN_REPO_URL:=$localRepoUrl}
# repo id is ignored by local repo
repoId=${GWT_MAVEN_REPO_ID:=none}

# use GWT_DIST_FILE to specify the default distribution file
gwtTrunk=$(dirname $(pwd))
gwtPathDefault=${GWT_DIST_FILE:=$(ls -t1 ${gwtTrunk}/build/dist/gwt-*.zip 2>/dev/null | head -1)}

VERSION_REGEX='[0-9][0-9]*\.[0-9][0-9]*\.[0-9][0-9]*-*.*'

# use GWT_VERSION to specify the default version or get it from the file name
gwtVersionDefault=${GWT_VERSION:=$(expr "$gwtPathDefault" : '.*gwt-\('$VERSION_REGEX'\)\.zip')}

# prompt for info
read -e -p"GWT version for Maven (${gwtVersionDefault:-ex: HEAD-SNAPSHOT}): " gwtVersion
gwtVersion=${gwtVersion:=$gwtVersionDefault}
if test "$gwtVersion" != "HEAD-SNAPSHOT" && ! expr "$gwtVersion" : "$VERSION_REGEX" >/dev/null; then
  echo "Please enter a version of the form x.y.z or x.y.z-abc"
  exit 1
fi

read -e -p"Deploy to repo URL ($repoUrlDefault): " repoUrl
repoUrl=${repoUrl:=$repoUrlDefault}

# setting the repoUrl to 'install' will instruct to maven-gwt to
# execute the install goal instead of the deploy one.
if [[ "$repoUrl" == "$localRepoUrl" ]]; then
  repoUrl=install
fi

# use GWT_GPG_PASS environment var by default if set
read -p"GPG passphrase for jar signing (may skip for local deployment): " gpgPassphrase
gpgPassphrase=${gpgPassphrase:=$GWT_GPG_PASS}

# A simplified maven-gwt is inlined here, since only poms are templated and deployed

gwtMavenVersion="$gwtVersion"
mavenRepoUrl="$repoUrl"
mavenRepoId="$repoId"

# Generate POMs with correct version
for template in `find $pomDir -name pom-template.xml`
do
  dir=`dirname $template`
  pushd $dir > /dev/null
  sed -e "s|\${gwtVersion}|$gwtMavenVersion|g" pom-template.xml >pom.xml
  popd > /dev/null
done

# push parent poms
maven-deploy-file $mavenRepoUrl $mavenRepoId $pomDir/gwt/pom.xml $pomDir/gwt/pom.xml || die

# push artifact relocation poms for gwt
for i in dev user servlet codeserver
do
  gwtPomFile=$pomDir/gwt/gwt-$i/pom.xml
  maven-deploy-file $mavenRepoUrl $mavenRepoId $gwtPomFile $gwtPomFile || die

done

# Deploy RequestFactory jars
maven-deploy-file $mavenRepoUrl $mavenRepoId $pomDir/requestfactory/pom.xml $pomDir/requestfactory/pom.xml || die

for i in client server apt
do
  rfPomFile=$pomDir/requestfactory/${i}/pom.xml
  maven-deploy-file $mavenRepoUrl $mavenRepoId $rfPomFile $rfPomFile || die
done

popd >/dev/null 2>&1
