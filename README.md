Check Design Rules
==================

Provides annotations that help you to enforce your design pattern rules by throwing errors at compilation time if needed.

<h3>Install</h3>
<p>Add dependency to maven pom.xml</p>
<pre>
  &lt;dependency&gt;
    &lt;groupId&gt;org.checkdesignrules&lt;/groupId&gt;
    &lt;artifactId&gt;checkdesignrules&lt;/artifactId&gt;
    &lt;version&gt;0.1-SNAPSHOT&lt;/version&gt;
    &lt;scope&gt;compile&lt;/scope&gt;
  &lt;/dependency&gt;
</pre>
<p>If it does not seems to work (no error when there should have) check that you do not have the compiler option <strong>-proc:none</strong></p>

<h3>CheckDependencies</h3>
<p>Package annotation that permit you to deny some dependencies to some other packages.</p>
<p>To use it, create or modify a file named package-info.java</p>
<pre>
@CheckDependencies(deny = "com.model.*")
package com.view.controller;

import org.cdr.CheckDependencies;
</pre>

<h3>SerializableClasses</h3>
<p>Package annotation that requires every class of the package to implements Serializable.</p>

[ ![Codeship Status for jherry/checkdesignrules](https://www.codeship.io/projects/c6e3fda0-21a6-0132-000d-063d8b748863/status)](https://www.codeship.io/projects/36317)
