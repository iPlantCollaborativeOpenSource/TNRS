set -e
mvn clean compile install
mvn dependency:copy-dependencies
cp target/dependency/* target/
#cp target/*.jar dist
#cd dist
#sh start.sh