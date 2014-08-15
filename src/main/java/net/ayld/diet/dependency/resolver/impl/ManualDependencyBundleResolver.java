package net.ayld.diet.dependency.resolver.impl;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarFile;

import net.ayld.diet.bundle.JarExploder;
import net.ayld.diet.dependency.matcher.DependencyMatcherStrategy;
import net.ayld.diet.dependency.resolver.DependencyBundleResolver;
import net.ayld.diet.model.ClassFile;
import net.ayld.diet.model.ClassName;
import net.ayld.diet.model.ExplodedJar;
import net.ayld.diet.util.annotation.ThreadSafe;

import org.springframework.beans.factory.annotation.Required;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

@ThreadSafe
public class ManualDependencyBundleResolver implements DependencyBundleResolver{

	private JarExploder jarExploder;
	private DependencyMatcherStrategy dependencyMatcher;
	
	@Override
	public Set<JarFile> resolve(ClassName className, Set<JarFile> bundles) throws IOException {
		final Set<JarFile> result = Sets.newHashSet();
		for (ExplodedJar explodedJar : jarExploder.explode(bundles)) {
			
			final File extractedJarDir = new File(explodedJar.getExtractedPath());
			
			if (containsDependency(extractedJarDir, className.toString(), new HashSet<Boolean>())) {
				result.add(explodedJar.getArchive());
			}
		}
		return ImmutableSet.copyOf(result);
	}
	
	private boolean containsDependency(File dir, String dependencyClassName, Set<Boolean> results) throws IOException {

        final File root = new File(dir.getAbsolutePath());
        final File[] children = root.listFiles();

        for (File child : children) {
        	
            if (child.isDirectory()) {
            	results.add(containsDependency(child, dependencyClassName, results));
            }
            else {
            	try {
					if (ClassFile.isClassfile(child)) {
						if (dependencyMatcher.matches(new ClassName(dependencyClassName), ClassFile.fromFilepath(child.getAbsolutePath()))) {
							return true;
						}
					}
				} catch (URISyntaxException e) {
					throw new IOException(e);
				}
            }
        }
        return results.contains(Boolean.TRUE);
    }
	
	@Required
	public void setDependencyMatcher(DependencyMatcherStrategy matcher) {
		this.dependencyMatcher = matcher;
	}

	@Required
	public void setJarExploder(JarExploder jarExploder) {
		this.jarExploder = jarExploder;
	}
}
