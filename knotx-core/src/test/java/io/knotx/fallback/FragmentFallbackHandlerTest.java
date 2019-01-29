/*
 * Copyright (C) 2016 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.knotx.fallback;

import io.knotx.dataobjects.KnotContext;
import io.knotx.junit.util.FileReader;
//import io.knotx.junit.util.KnotContextFactory;
import io.knotx.junit5.KnotxApplyConfiguration;
import io.knotx.options.FallbackMetadata;
import io.vertx.reactivex.ext.web.RoutingContext;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * TODO Test extracted from Assembler, must be refactored
 */
public class FragmentFallbackHandlerTest {

  private final static String FRAGMENT_RAW = "_raw";
  private final static String FALLBACK_KNOT = "_fallback";
  private static final String SERVICE_KNOT = "services";
  private static final String HANDLEBARS_KNOT = "handlebars";
  private static final String DEFAULT_FALLBACK_MESSAGE = "<p class='error'>default fallback</p>";
  private static final String CUSTOM_FALLBACK_MESSAGE = "<p class='error'>custom fallback</p>";
  private final static String DEFAULT_FALLBACK_ID = "BLANK";
  private static final String CUSTOM_FALLBACK_ID = "FALLBACK_1";

//  @Test
//  @KnotxApplyConfiguration("io/knotx/assembler/test.asIs.io.knotx.FragmentAssembler.json")
//  public void callHandlerWithFailedSnippet_expectDefaultFallback() throws IOException {
//    // when
//    List<Triple<List<String>, String, String>> fragments = Arrays.asList(
//        toTriple("io/knotx/assembler/fragment1.txt", null,
//            FRAGMENT_RAW),
//        toTriple("io/knotx/assembler/fragment2.txt", DEFAULT_FALLBACK_ID,
//            SERVICE_KNOT, HANDLEBARS_KNOT),
//        toTriple("io/knotx/assembler/fragment3.txt", null,
//            FRAGMENT_RAW));
//
//    // we use the default fallback mechanism that replace the content with empty string
//    FragmentFallbackHandlerOptions options = new FragmentFallbackHandlerOptions();
//    options.setDefaultFallback(DEFAULT_FALLBACK_ID);
//
//    final KnotContext knotContext = KnotContextFactory.create(fragments);
//
//    RoutingContext mockContext = Mockito.mock(RoutingContext.class);
//    Mockito.when(mockContext.get(KnotContext.KEY)).thenReturn(knotContext);
//
//    // when
//    new FragmentFallbackHandler(options).handle(mockContext);
//
//    // then
//    KnotContext result = mockContext.get(KnotContext.KEY);
//    result.getFragments().stream()
//        .filter(fragment -> !fragment.isRaw() && !fragment.isFallback())
//        .forEach(
//            fragment -> Assert.assertEquals(StringUtils.EMPTY, fragment.content()));
//  }
//
//  @Test
//  public void callHandlerWithFailedSnippet_expectCustomFallback() throws IOException {
//    // when
//    List<Triple<List<String>, String, String>> fragments = Arrays.asList(
//        toTriple("io/knotx/assembler/fragment1.txt", null, FRAGMENT_RAW),
//        toTriple("io/knotx/assembler/fragment2.txt", CUSTOM_FALLBACK_ID, SERVICE_KNOT,
//            HANDLEBARS_KNOT),
//        toTriple("io/knotx/assembler/fragment3.txt", null, FRAGMENT_RAW),
//        toTriple("io/knotx/assembler/fallback.txt", null, FALLBACK_KNOT));
//
//    FragmentFallbackHandlerOptions options = new FragmentFallbackHandlerOptions();
//    options.setDefaultFallback(DEFAULT_FALLBACK_ID);
//    options.setFallbacks(Collections
//        .singletonList(new FallbackMetadata(DEFAULT_FALLBACK_ID, DEFAULT_FALLBACK_MESSAGE)));
//    options.setFallbacks(Collections
//        .singletonList(new FallbackMetadata(CUSTOM_FALLBACK_ID, CUSTOM_FALLBACK_MESSAGE)));
//
//    final KnotContext knotContext = KnotContextFactory.create(fragments);
//
//    RoutingContext mockContext = Mockito.mock(RoutingContext.class);
//    Mockito.when(mockContext.get(KnotContext.KEY)).thenReturn(knotContext);
//
//    // when
//    new FragmentFallbackHandler(options).handle(mockContext);
//
//    // then
//    KnotContext result = mockContext.get(KnotContext.KEY);
//    result.getFragments().stream()
//        .filter(fragment -> !fragment.isRaw() && !fragment.isFallback())
//        .forEach(
//            fragment -> Assert.assertEquals(CUSTOM_FALLBACK_MESSAGE + "\n", fragment.content()));
//  }
//
//  private Triple<List<String>, String, String> toTriple(String filePath, String failed,
//      String... knots) throws IOException {
//    return new ImmutableTriple<>(Arrays.asList(knots), failed, FileReader.readText(filePath));
//  }
}
