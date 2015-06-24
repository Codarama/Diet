package org.codarama.diet.resolver.impl;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Set;
import java.util.jar.JarFile;

import org.codarama.diet.dependency.resolver.DependencyBundleResolver;
import org.codarama.diet.model.ClassName;
import org.codarama.diet.util.Tokenizer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.io.Resources;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:META-INF/test-contexts/testManualDependencyBundleResolverContext.xml"})
public class TestManualDependencyBundleResolver {
	
	@Autowired
	private DependencyBundleResolver bundleResolver;
	
	@Autowired
	private String workDir;
	
	@Test
	public void resolve() throws IOException, URISyntaxException {
		
		final Set<URL> bundleUrls = ImmutableSet.of(
				Resources.getResource("test-classes/lib/aspectjweaver-1.6.12.jar"),
				Resources.getResource("test-classes/lib/commons-lang3-3.1.jar"),
				Resources.getResource("test-classes/lib/primefaces-3.5.jar")
		);

		final Set<JarFile> bundles = Sets.newHashSet();
		for (URL bundleUrl : bundleUrls) {
			
			// make sure extracted folders are not already created
			final String jarName = Tokenizer.delimiter("/").tokenize(bundleUrl.toString()).lastToken();
			final File extractedDir = new File(Joiner.on(File.separator).join(workDir, jarName));
			if (extractedDir.exists()) {
				delete(extractedDir);
			}
			
			// make URLs to files
			bundles.add(new JarFile(new File(bundleUrl.toURI())));
		}
		
		final Set<JarFile> jarsContainingClass = bundleResolver.resolve(new ClassName("org.primefaces.context.PrimePartialViewContext"), bundles);
		
		assertTrue(jarsContainingClass.size() > 0);
		assertTrue(jarsContainingClass.iterator().next().getName().endsWith("primefaces-3.5.jar"));
	}
	
	private static void delete(File file) throws IOException {
		if (file.isDirectory()) {
			final File[] files = file.listFiles();
			if (files == null) {
				return;
			}
			for (File sub : files) {
				delete(sub);
			}
		}
		if (!file.delete()) {
			throw new IOException("failed to deleteRecursive: " + file);
		}
	}
}
