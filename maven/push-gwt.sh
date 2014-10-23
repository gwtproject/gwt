#!/bin/bash
#
# Pushes GWT artifacts to a local (the default) or remote maven repository
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

export pomDir=./poms

source lib-gwt.sh

# use GWT_MAVEN_REPO_URL if set else M2_REPO else default location for local repo
localRepoUrl=${M2_REPO:="$HOME/.m2/repository"}
localRepoUrl="file://$localRepoUrl"
repoUrlDefault=${GWT_MAVEN_REPO_URL:=$localRepoUrl}
# repo id is ignored by local repo
repoId=${GWT_MAVEN_REPO_ID:=none}

gwtTrunk=$(dirname $(pwd))
gwtPathDefault=$(ls -r1 ${gwtTrunk}/build/dist/gwt-*.zip 2>/dev/null | head -1)
if [[ -f "$gwtPathDefault" ]]; then
  gwtPathPrompt="($gwtPathDefault)"
  gwtVersionDefault=$(echo $gwtPathDefault | sed -e 's/.*gwt-\(.*\).zip/\1/')
  gwtVersionPrompt="$gwtVersionDefault"
else
  gwtVersionPrompt="ex: 2.8.0-SNAPSHOT"
fi

# prompt for info
read -e -p"GWT version for Maven ($gwtVersionPrompt): " gwtVersion
case $gwtVersion in
  "")
    gwtVersion=$gwtVersionDefault
    ;;
  *.*.*|*.*.*-*)
    ;;
  *)
    gwtVersion=""
    ;;
esac
if [[ -z "$gwtVersion" ]]; then
  echo "Please enter a version of the form x.y.z or x.y.z-abc"
  exit 1
fi

read -e -p"Path to GWT distro zip $gwtPathPrompt: " gwtPath
case $gwtPath in
  "" )
    gwtPath=$gwtPathDefault
    ;;
  * )
    ;;
esac
if [[ "$gwtPath" == "" || ! -f  $gwtPath ]]; then
  echo "ERROR: Cannot find file at \"$gwtPath\""
  exit 1
fi

read -e -p"Deploy to repo URL ($repoUrlDefault): " repoUrl
case $repoUrl in
  "" )
    repoUrl=$repoUrlDefault
    ;;
  * )
    ;;
esac

# setting the repoUrl to 'install' will instruct to maven-gwt to
# execute the install goal instead of the deploy one.
if [[ "$repoUrl" == "$localRepoUrl" ]]; then
  repoUrl=install
fi

read -p"GPG passphrase for jar signing (may skip for local deployment): " gpgPassphrase

maven-gwt "$gwtVersion" \
          "$gwtPath" \
          "$repoUrl" \
          "$repoId"

popd >/dev/null 2>&1
