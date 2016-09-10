# spring-dependencies

Very simple Maven plugin to print your Spring import dependencies on the commandline.

  <build>
         <plugins>
             <plugin>
                 <artifactId>spring-dependency</artifactId>
                 <groupId>ch.christianmenz</groupId>
                 <version>1.0-SNAPSHOT</version>      
                 
                 <configuration>
                     <configLocations>
                         <param>application.xml</param>
                     </configLocations>
                     <printUrl>false</printUrl>
                 </configuration>    
                 <executions>
                     <execution>
                         <phase>install</phase>
                         <goals>
                             <goal>list</goal>
                         </goals>
                     </execution>
                 </executions>                   
             </plugin>
         </plugins>
     </build>
This will print something like:
--- spring-dependency:1.0-SNAPSHOT:list (default) @ demoMaven ---
application.xml
  module1.xml
    module2.xml
