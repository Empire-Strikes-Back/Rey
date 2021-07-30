#!/bin/bash

rename(){
    echo $1
}

repl(){
  clj -M:repl
}

main(){
  clojure -M:main
}

uberjar(){

  # clojure -X:depstar uberjar \
  #   :aot true \
  #   :jar target/lochdown.standalone.jar \
  #   :verbose false \
  #   :main-class lochdown.main


  lein with-profiles +prod uberjar
  mkdir -p target/jpackage-input
  mv target/lochdown.standalone.jar target/jpackage-input/
  #  java -Dclojure.core.async.pool-size=1 -jar target/lochdown-standalone.jar
}

j-package(){
  OS=${1:?"Need OS type (windows/linux/mac)"}

  echo "Starting build..."

  if [ "$OS" == "windows" ]; then
    J_ARG="--win-menu --win-dir-chooser --win-shortcut"
          
  elif [ "$OS" == "linux" ]; then
      J_ARG="--linux-shortcut"
  else
      J_ARG=""
  fi

  APP_VERSION=0.1.0

  jpackage \
    --input target/jpackage-input \
    --dest target \
    --main-jar lochdown.standalone.jar \
    --name "lochdown" \
    --main-class clojure.main \
    --arguments -m \
    --arguments lochdown.main \
    --resource-dir resources \
    --java-options -Xmx2048m \
    --app-version ${APP_VERSION} \
    $J_ARG
  
}

push(){
  ORIGIN=$(git remote get-url origin)
  rm -rf .git
  git init -b main
  git remote add origin $ORIGIN
  git config --local include.path ../.gitconfig
  git add .
  git commit -m "i am lochdown program"
  git push -f -u origin main
}

"$@"