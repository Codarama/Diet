package net.ayld.facade.dependency.resolver.impl;

import java.io.IOException;
import java.util.Set;

import net.ayld.facade.component.ListenableComponent;
import net.ayld.facade.dependency.resolver.DependencyResolver;
import net.ayld.facade.event.model.SourceResolverUpdate;
import net.ayld.facade.model.ClassName;
import net.ayld.facade.model.SourceFile;
import net.ayld.facade.util.Tokenizer;
import net.ayld.facade.util.annotation.ThreadSafe;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.io.Resources;

@ThreadSafe
public class ManualParseSourceDependencyResolver extends ListenableComponent implements DependencyResolver<SourceFile>{

	@Override
	public Set<ClassName> resolve(SourceFile source) throws IOException {
		eventBus.post(new SourceResolverUpdate("resolving: " + source.physicalFile().getAbsolutePath()));
		
		final String sourceFileContent = Resources.toString(source.physicalFile().toURI().toURL(), Charsets.UTF_8);
		
		// we can somehow select only lines starting with import so we don't need to iterate over every single line
		final Set<ClassName> result = Sets.newHashSet();
		for (String line : Splitter.on("\n").split(sourceFileContent)) {
			
			if (line.startsWith(SourceFile.IMPORT_KEYWOD)) {
				
				if (line.endsWith(SourceFile.WILDCARD_IMPORT_SUFFIX)) {
					throw new IllegalArgumentException("wildcard imports: " + line + ", not currently supported");
				}
				
				final String dependency = Tokenizer.delimiter(" ").tokenize(line).lastToken().replaceAll(";", "");
				result.add(new ClassName(dependency));
			}
		}
		
		eventBus.post(new SourceResolverUpdate("resolved: " + result));
		
		return ImmutableSet.copyOf(result);
	}

	@Override
	public Set<ClassName> resolve(Set<SourceFile> sources) throws IOException {
		final Set<ClassName> result = Sets.newHashSet();
		
		for (SourceFile source : sources) {
			result.addAll(resolve(source));
		}
		
		return result;
	}
}
