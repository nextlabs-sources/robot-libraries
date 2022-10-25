## Project Setup
### Checkout the project
Checkout the project from Git repository.
### Open the project in intellij idea
The project is a maven project with multiple modules.
### Build the project
To build the project, cd into the project and execute: (you need to have maven installed)

    mvn package

### Build and copy required artifacts to automation kit
You need to set environment variable **AUTOMATIONKIT_DIR** to the automation kit project folder. Then cd into the project and execute: (same as before, you need to have maven installed)

    mvn install

### In-Project Repository
The project uses some external libraries which are not in public maven repository. So the project stores them inside **libs** folder under project base folder. To add your own third-party jars into the in-project repository, you can follow following steps:

Assume you have an external library as a jar file whose name is **nlJavaSDK2.jar**.

Firstly, you need to give (make up) it a groupId, an artifactId and a version. For example, you can give nlJavaSDK2.jar following values:

 - groupId: **com.nextlabs**
 - artifactId: **java-sdk2**
 - version: **1**

Then, use maven deploy plugin to **deploy** the jar into the project **libs** folder.

    mvn deploy:deploy-file -Durl=file:/path/to/project/libs/ -Dfile=/path/to/nlJavaSDK2.jar -DgroupId=com.nextlabs -DartifactId=java-sdk2 -Dversion=1.0 -Dpackaging=jar

Next, modify project **pom.xml** to add the dependency to the project. You need to add following xml portion to the root project's **\<dependencies\>** element under **\<dependencyManagement\>** element.

    <dependency>
      <groupId>com.nextlabs</groupId>
      <artifactId>java-sdk2</artifactId>
      <version>1.0</version>
    </dependency>

Finally, modify the **pom.xml** under child project (module under the root project) who has the dependency on this library. Add the same xml protion but remove the version to child pom.xml's   **\<dependencies\>** element.

Also make sure the child pom has this following xml element in it's **\<properties\>**.

    <main.basedir>${project.basedir}/..</main.basedir>

The reason to add this property is that inside the parent pom, the in-project repositoy url is defined as **file:${main.basedir}/libs**, to let the child module to correctly find the repository you need to override the **main.basedir** property.

