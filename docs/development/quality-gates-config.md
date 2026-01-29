# 🔧 Configuración de Quality Gates - Maven Integration

## Configuración completa para integrar Checkstyle + SpotBugs + JaCoCo + SonarQube

### 📋 pom.xml Configuration

```xml
<!-- Agregar al pom.xml existente -->
<properties>
    <!-- Quality Gates Properties -->
    <maven.compiler.source>24</maven.compiler.source>
    <maven.compiler.target>24</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>

    <!-- Plugin Versions -->
    <checkstyle.version>3.4.0</checkstyle.version>
    <spotbugs.version>4.8.6.2</spotbugs.version>
    <jacoco.version>0.8.12</jacoco.version>
    <sonar.version>4.0.0.4121</sonar.version>

    <!-- Quality Thresholds -->
    <jacoco.coverage.minimum>0.80</jacoco.coverage.minimum>
    <checkstyle.failOnViolation>true</checkstyle.failOnViolation>
    <spotbugs.failOnError>true</spotbugs.failOnError>

    <!-- SonarQube Properties -->
    <sonar.projectKey>apitickets</sonar.projectKey>
    <sonar.organization>akc9912</sonar.organization>
    <sonar.host.url>https://sonarcloud.io</sonar.host.url>
    <sonar.coverage.jacoco.xmlReportPaths>target/site/jacoco/jacoco.xml</sonar.coverage.jacoco.xmlReportPaths>
    <sonar.java.checkstyle.reportPaths>target/checkstyle-result.xml</sonar.java.checkstyle.reportPaths>
    <sonar.java.spotbugs.reportPaths>target/spotbugsXml.xml</sonar.java.spotbugs.reportPaths>
</properties>

<build>
    <plugins>
        <!-- JaCoCo Coverage Plugin -->
        <plugin>
            <groupId>org.jacoco</groupId>
            <artifactId>jacoco-maven-plugin</artifactId>
            <version>${jacoco.version}</version>
            <executions>
                <execution>
                    <goals>
                        <goal>prepare-agent</goal>
                    </goals>
                </execution>
                <execution>
                    <id>report</id>
                    <phase>verify</phase>
                    <goals>
                        <goal>report</goal>
                    </goals>
                </execution>
                <execution>
                    <id>coverage-check</id>
                    <phase>verify</phase>
                    <goals>
                        <goal>check</goal>
                    </goals>
                    <configuration>
                        <rules>
                            <rule>
                                <element>BUNDLE</element>
                                <limits>
                                    <limit>
                                        <counter>INSTRUCTION</counter>
                                        <value>COVEREDRATIO</value>
                                        <minimum>${jacoco.coverage.minimum}</minimum>
                                    </limit>
                                </limits>
                            </rule>
                        </rules>
                    </configuration>
                </execution>
            </executions>
        </plugin>

        <!-- Checkstyle Plugin -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-checkstyle-plugin</artifactId>
            <version>${checkstyle.version}</version>
            <configuration>
                <configLocation>checkstyle.xml</configLocation>
                <includeTestSourceDirectory>false</includeTestSourceDirectory>
                <failOnViolation>${checkstyle.failOnViolation}</failOnViolation>
                <consoleOutput>true</consoleOutput>
                <outputFile>target/checkstyle-result.xml</outputFile>
                <outputFileFormat>xml</outputFileFormat>
            </configuration>
            <executions>
                <execution>
                    <id>checkstyle-check</id>
                    <phase>verify</phase>
                    <goals>
                        <goal>check</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>

        <!-- SpotBugs Plugin -->
        <plugin>
            <groupId>com.github.spotbugs</groupId>
            <artifactId>spotbugs-maven-plugin</artifactId>
            <version>${spotbugs.version}</version>
            <configuration>
                <effort>Max</effort>
                <threshold>Low</threshold>
                <failOnError>${spotbugs.failOnError}</failOnError>
                <xmlOutput>true</xmlOutput>
                <xmlOutputDirectory>target</xmlOutputDirectory>
                <excludeFilterFile>spotbugs-exclude.xml</excludeFilterFile>
            </configuration>
            <executions>
                <execution>
                    <id>spotbugs-check</id>
                    <phase>verify</phase>
                    <goals>
                        <goal>check</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>

        <!-- SonarQube Plugin -->
        <plugin>
            <groupId>org.sonarsource.scanner.maven</groupId>
            <artifactId>sonar-maven-plugin</artifactId>
            <version>${sonar.version}</version>
        </plugin>

        <!-- Surefire Plugin for Tests -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>3.1.2</version>
            <configuration>
                <skipTests>false</skipTests>
                <includes>
                    <include>**/*Test.java</include>
                    <include>**/*Tests.java</include>
                </includes>
            </configuration>
        </plugin>
    </plugins>
</build>
```

## 📁 Configuration Files

### checkstyle.xml
```xml
<?xml version="1.0"?>
<!DOCTYPE module PUBLIC "-//Checkstyle//DTD Checkstyle Configuration 1.3//EN"
        "https://checkstyle.org/dtds/configuration_1_3.dtd">
<module name="Checker">
    <property name="severity" value="error"/>
    <property name="fileExtensions" value="java, properties, xml"/>

    <!-- Suppressions -->
    <module name="SuppressionFilter">
        <property name="file" value="checkstyle-suppressions.xml"/>
        <property name="optional" value="true"/>
    </module>

    <!-- File Tab Character -->
    <module name="FileTabCharacter">
        <property name="eachLine" value="true"/>
    </module>

    <module name="TreeWalker">
        <!-- Naming Conventions -->
        <module name="ConstantName"/>
        <module name="LocalFinalVariableName"/>
        <module name="LocalVariableName"/>
        <module name="MemberName"/>
        <module name="MethodName"/>
        <module name="PackageName"/>
        <module name="ParameterName"/>
        <module name="StaticVariableName"/>
        <module name="TypeName"/>

        <!-- Imports -->
        <module name="AvoidStarImport"/>
        <module name="IllegalImport"/>
        <module name="RedundantImport"/>
        <module name="UnusedImports"/>

        <!-- Size Violations -->
        <module name="LineLength">
            <property name="max" value="120"/>
        </module>
        <module name="MethodLength">
            <property name="max" value="150"/>
        </module>

        <!-- Whitespace -->
        <module name="EmptyForIteratorPad"/>
        <module name="GenericWhitespace"/>
        <module name="MethodParamPad"/>
        <module name="NoWhitespaceAfter"/>
        <module name="NoWhitespaceBefore"/>
        <module name="OperatorWrap"/>
        <module name="ParenPad"/>
        <module name="TypecastParenPad"/>
        <module name="WhitespaceAfter"/>
        <module name="WhitespaceAround"/>

        <!-- Modifier Checks -->
        <module name="ModifierOrder"/>
        <module name="RedundantModifier"/>

        <!-- Blocks -->
        <module name="AvoidNestedBlocks"/>
        <module name="EmptyBlock"/>
        <module name="LeftCurly"/>
        <module name="NeedBraces"/>
        <module name="RightCurly"/>

        <!-- Common Coding Problems -->
        <module name="EmptyStatement"/>
        <module name="EqualsHashCode"/>
        <module name="HiddenField">
            <property name="ignoreConstructorParameter" value="true"/>
            <property name="ignoreSetter" value="true"/>
        </module>
        <module name="IllegalInstantiation"/>
        <module name="InnerAssignment"/>
        <module name="MissingSwitchDefault"/>
        <module name="SimplifyBooleanExpression"/>
        <module name="SimplifyBooleanReturn"/>

        <!-- Class Design -->
        <module name="FinalClass"/>
        <module name="HideUtilityClassConstructor"/>
        <module name="InterfaceIsType"/>
        <module name="VisibilityModifier"/>

        <!-- Miscellaneous -->
        <module name="ArrayTypeStyle"/>
        <module name="TodoComment"/>
        <module name="UpperEll"/>
    </module>
</module>
```

### spotbugs-exclude.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<FindBugsFilter>
    <!-- Exclude test classes -->
    <Match>
        <Class name="~.*Test.*"/>
    </Match>

    <!-- Exclude generated classes -->
    <Match>
        <Class name="~.*\$.*"/>
    </Match>

    <!-- Exclude specific patterns that are acceptable in our codebase -->
    <Match>
        <Bug pattern="SE_BAD_FIELD"/>
        <Class name="~.*Entity"/>
    </Match>

    <!-- Spring Boot specific exclusions -->
    <Match>
        <Bug pattern="NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"/>
        <Class name="~.*Controller"/>
    </Match>
</FindBugsFilter>
```

## 🚀 Commands & Usage

### Local Development
```bash
# Ejecutar quality checks localmente
mvn clean verify

# Solo coverage
mvn clean test jacoco:report

# Solo checkstyle
mvn checkstyle:check

# Solo spotbugs
mvn spotbugs:check

# Generar todos los reportes sin fallar
mvn clean verify -Dcheckstyle.failOnViolation=false -Dspotbugs.failOnError=false
```

### SonarQube Integration
```bash
# Ejecutar análisis completo con SonarQube
mvn clean verify sonar:sonar \
  -Dsonar.token=your_token_here

# Con variables de entorno
export SONAR_TOKEN=your_token_here
mvn clean verify sonar:sonar
```

## 🔄 CI/CD Integration Examples

### GitHub Actions
```yaml
name: Quality Gates

on: [push, pull_request]

jobs:
  quality-check:
    runs-on: ubuntu-latest

    steps:
    - uses: actions/checkout@v4
      with:
        fetch-depth: 0

    - name: Set up JDK 24
      uses: actions/setup-java@v4
      with:
        java-version: '24'
        distribution: 'temurin'

    - name: Cache Maven packages
      uses: actions/cache@v4
      with:
        path: ~/.m2
        key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}

    - name: Run Quality Gates
      run: mvn clean verify

    - name: SonarQube Analysis
      env:
        SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
      run: mvn sonar:sonar

    - name: Quality Gate Check
      run: |
        # Wait for SonarQube quality gate result
        curl -u ${{ secrets.SONAR_TOKEN }}: \
          "https://sonarcloud.io/api/qualitygates/project_status?projectKey=apitickets" \
          | jq -e '.projectStatus.status == "OK"'
```

### Jenkins Pipeline
```groovy
pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/Akc9912/apiTickets.git'
            }
        }

        stage('Quality Gates') {
            steps {
                sh 'mvn clean verify'
            }
            post {
                always {
                    publishHTML([
                        allowMissing: false,
                        alwaysLinkToLastBuild: false,
                        keepAll: true,
                        reportDir: 'target/site/jacoco',
                        reportFiles: 'index.html',
                        reportName: 'JaCoCo Coverage Report'
                    ])

                    recordIssues(
                        enabledForFailure: true,
                        tools: [
                            checkStyle(pattern: 'target/checkstyle-result.xml'),
                            spotBugs(pattern: 'target/spotbugsXml.xml')
                        ]
                    )
                }
                failure {
                    error 'Quality gates failed. Pipeline stopped.'
                }
            }
        }

        stage('SonarQube Analysis') {
            when {
                expression { currentBuild.result == null || currentBuild.result == 'SUCCESS' }
            }
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh 'mvn sonar:sonar'
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 1, unit: 'HOURS') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Deploy') {
            when {
                expression { currentBuild.result == null || currentBuild.result == 'SUCCESS' }
            }
            steps {
                sh 'docker build -t apitickets:latest .'
                sh 'docker-compose up -d'
            }
        }
    }
}
```

## 📊 Quality Gate Rules (SonarQube)

### Conditions for Passing
```yaml
Coverage: > 80%
Duplicated Lines: < 3%
Maintainability Rating: A
Reliability Rating: A
Security Rating: A
Security Hotspots: Reviewed
New Code Coverage: > 80%
New Duplicated Lines: < 3%
```

### Setup Instructions
1. **Add plugins to pom.xml** ✅
2. **Create configuration files** ✅
3. **Configure SonarQube project** ✅
4. **Setup CI/CD pipeline** ✅
5. **Configure quality gate rules** ✅

**Result**: `mvn verify` ejecuta todas las herramientas localmente, SonarQube consume los reportes y aplica el quality gate, CI/CD bloquea si falla.
