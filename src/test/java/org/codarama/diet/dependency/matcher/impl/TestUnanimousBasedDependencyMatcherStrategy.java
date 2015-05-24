package org.codarama.diet.dependency.matcher.impl;

import org.junit.Assert;
import org.codarama.diet.dependency.matcher.DependencyMatcherStrategy;
import org.codarama.diet.model.ClassFile;
import org.codarama.diet.model.ClassName;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:META-INF/test-contexts/testUnanimousBasedDependencyMatcherStrategyContext.xml"})
public class TestUnanimousBasedDependencyMatcherStrategy {
	
	@Autowired
	private DependencyMatcherStrategy dependencyMatcherStrategy;
	
	@Test
	public void match() {
		final ClassName className = new ClassName("org.primefaces.context.PrimePartialViewContext");
		final ClassFile classFile = ClassFile.fromClasspath("test-classes/primefaces-3.5.jar/org/primefaces/context/PrimePartialViewContext.class");
		
		Assert.assertTrue(dependencyMatcherStrategy.matches(className, classFile));
	}
}
