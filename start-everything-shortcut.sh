# use like below to rebuild the jar: 
# ./start-everything-shortcut.sh rebuild
# OR 
# ./start-everything-shortcut.sh # does not rebuild the jar

./scripts/startup/start-every-compose.sh ${1-"do-not-rebuild"}
