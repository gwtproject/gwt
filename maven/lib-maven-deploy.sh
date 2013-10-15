# Locate Maven exe
export MAVEN_BIN=${MAVEN_BIN:=`which mvn`}
if [ -z "$MAVEN_BIN" ]; then
  echo "mvn not found. Add mvn to PATH or set MAVEN_BIN."
  exit
fi
echo Using $MAVEN_BIN

function set-random-dir() {
  export RANDOM_DIR=/tmp/random-dir-$RANDOM$RANDOM$RANDOM$RANDOM
  rm -rf $RANDOM_DIR
  mkdir -p $RANDOM_DIR
}

function maven-deploy-file() {
  local mavenRepoUrl=$1
  shift
  local mavenRepoId=$1
  shift
  local curFile=$1
  shift
  local pomFile=$1
  shift
  local javadoc=$1
  shift
  local sources=$1
  shift

  if [[ "$curFile" == "" ]]; then
    echo "ERROR: Unable to deploy $artifactId in repo! Cannot find corresponding file!"
    return 1
  fi

  local cmd="";
  if [[ "$gpgPassphrase" != "" ]]; then
    cmd="$MAVEN_BIN \
           org.apache.maven.plugins:maven-gpg-plugin:1.4:sign-and-deploy-file \
            -Dfile=$curFile \
            -Durl=$mavenRepoUrl \
            -DrepositoryId=$mavenRepoId \
            -DpomFile=$pomFile \
            -DuniqueVersion=false \
            $javadoc \
            $sources \
            -Dgpg.passphrase=\"$gpgPassphrase\""
  else
    echo "GPG passphrase not specified; will attempt to deploy files without signing"
    cmd="$MAVEN_BIN \
           org.apache.maven.plugins:maven-deploy-plugin:2.7:deploy-file \
            -Dfile=$curFile \
            -Durl=$mavenRepoUrl \
            -DrepositoryId=$mavenRepoId \
            -DpomFile=$pomFile \
            -DuniqueVersion=false \
            $javadoc \
            $sources"
  fi
  echo $cmd
  eval $cmd
}

