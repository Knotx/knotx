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
package io.knotx.knot.action;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.google.common.collect.Lists;
import io.knotx.dataobjects.AdapterRequest;
import io.knotx.dataobjects.AdapterResponse;
import io.knotx.dataobjects.ClientResponse;
import io.knotx.dataobjects.Fragment;
import io.knotx.dataobjects.KnotContext;
import io.knotx.http.MultiMapCollector;
import io.knotx.junit.util.KnotContextFactory;
import io.knotx.junit5.KnotxApplyConfiguration;
import io.knotx.junit5.KnotxExtension;
import io.knotx.junit5.KnotxTestUtils;
import io.knotx.proxy.AdapterProxy;
import io.knotx.reactivex.proxy.KnotProxy;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.functions.Consumer;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.core.MultiMap;
import io.vertx.reactivex.core.Vertx;
import io.vertx.serviceproxy.ServiceBinder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;
import org.jsoup.parser.Parser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(KnotxExtension.class)
public class ActionKnotProxyVerticleTest {

  public static final String KNOT_TRANSITION = "next";
  private final static String ADDRESS = "knotx.knot.action";
  private final static String HIDDEN_INPUT_TAG_NAME = "snippet-identifier";
  private static final String FRAGMENT_KNOTS = "data-knotx-knots";
  private final static String FRAGMENT_REDIRECT_IDENTIFIER = "someId123";
  private final static Fragment FIRST_FRAGMENT = Fragment.raw("<html><head></head><body>");
  private final static Fragment LAST_FRAGMENT = Fragment.raw("</body></html>");
  private static final Document.OutputSettings OUTPUT_SETTINGS = new Document.OutputSettings()
      .escapeMode(Entities.EscapeMode.xhtml)
      .indentAmount(0)
      .prettyPrint(false);


  @Test
  @KnotxApplyConfiguration("knotx-test.json")
  public void callGetWithNoActionFragments_expectResponseOkNoFragmentChanges(
      VertxTestContext context, Vertx vertx) throws Exception {
    String expectedTemplatingFragment = KnotxTestUtils.readText("fragment_templating_out.txt");
    KnotContext knotContext = createKnotContext(FIRST_FRAGMENT, LAST_FRAGMENT,
        "fragment_templating_in.txt");
    knotContext.getClientRequest().setMethod(HttpMethod.GET);

    callActionKnotWithAssertions(context, vertx, knotContext,
        clientResponse -> {
          assertEquals(HttpResponseStatus.OK.code(),
              clientResponse.getClientResponse().getStatusCode());
          assertNotNull(clientResponse.getTransition());
          assertEquals(KNOT_TRANSITION, clientResponse.getTransition());
          assertNotNull(clientResponse.getFragments());

          List<Fragment> fragments = clientResponse.getFragments();
          assertEquals(FIRST_FRAGMENT.content(), fragments.get(0).content());
          assertEquals(expectedTemplatingFragment, fragments.get(1).content());
          assertEquals(LAST_FRAGMENT.content(), fragments.get(2).content());
        });
  }

  @Test
  @KnotxApplyConfiguration("knotx-test.json")
  public void callGetWithTwoActionFragments_expectResponseOkTwoFragmentChanges(
      VertxTestContext context, Vertx vertx) throws Exception {
    String expectedRedirectFormFragment = KnotxTestUtils.readText("fragment_form_redirect_out.txt");
    String expectedSelfFormFragment = KnotxTestUtils.readText("fragment_form_self_out.txt");
    KnotContext knotContext = createKnotContext(FIRST_FRAGMENT, LAST_FRAGMENT,
        "fragment_form_redirect_in.txt", "fragment_form_self_in.txt");
    knotContext.getClientRequest().setMethod(HttpMethod.GET);

    callActionKnotWithAssertions(context, vertx, knotContext,
        clientResponse -> {
          assertEquals(HttpResponseStatus.OK.code(),
              clientResponse.getClientResponse().getStatusCode());
          assertNotNull(clientResponse.getTransition());
          assertEquals(KNOT_TRANSITION, clientResponse.getTransition());
          assertNotNull(clientResponse.getFragments());

          List<Fragment> fragments = clientResponse.getFragments();
          assertEquals(FIRST_FRAGMENT.content(), fragments.get(0).content());
          assertEquals(clean(expectedRedirectFormFragment), clean(fragments.get(1).content()));
          assertEquals(clean(expectedSelfFormFragment), clean(fragments.get(2).content()));
          assertEquals(LAST_FRAGMENT.content(), fragments.get(3).content());
        });
  }

  @Test
  @KnotxApplyConfiguration("knotx-test.json")
  public void callGetWithActionFragmentWithoutIdentifier_expectResponseOkWithOneFragmentChanges(
      VertxTestContext context, Vertx vertx) throws Exception {
    KnotContext knotContext = createKnotContext("fragment_form_no_identifier_in.txt");
    String expectedFragmentHtml = KnotxTestUtils.readText("fragment_form_no_identifier_out.txt");
    knotContext.getClientRequest().setMethod(HttpMethod.GET);

    callActionKnotWithAssertions(context, vertx, knotContext,
        clientResponse -> {
          assertEquals(HttpResponseStatus.OK.code(),
              clientResponse.getClientResponse().getStatusCode());
          assertNotNull(clientResponse.getTransition());
          assertEquals(KNOT_TRANSITION, clientResponse.getTransition());
          assertNotNull(clientResponse.getFragments());

          List<Fragment> fragments = clientResponse.getFragments();
          assertEquals(fragments.size(), 1);
          assertEquals(clean(expectedFragmentHtml), clean(fragments.get(0).content()));
        });
  }

  @Test
  @KnotxApplyConfiguration("knotx-test.json")
  public void callGetWithActionFragmentActionHandlerNotExists_expectStatusCode500(
      VertxTestContext context, Vertx vertx) throws Exception {
    KnotContext knotContext = createKnotContext("fragment_form_actionhandler_not_exists_in.txt");
    knotContext.getClientRequest().setMethod(HttpMethod.GET);

    callActionKnotWithAssertions(context, vertx, knotContext,
        clientResponse -> {
          assertEquals(HttpResponseStatus.INTERNAL_SERVER_ERROR.code(),
              clientResponse.getClientResponse().getStatusCode());
          assertNull(clientResponse.getTransition());
          assertNull(clientResponse.getFragments());
        });
  }

  @Test
  @KnotxApplyConfiguration("knotx-test.json")
  public void callPostWithTwoActionFragments_expectResponseOkWithTransitionStep2(
      VertxTestContext context, Vertx vertx) throws Exception {
    createMockAdapter(vertx, "address-redirect", "", "step2");
    KnotContext knotContext = createKnotContext("fragment_form_redirect_in.txt",
        "fragment_form_self_in.txt");
    knotContext.getClientRequest()
        .setMethod(HttpMethod.POST)
        .setFormAttributes(MultiMap.caseInsensitiveMultiMap()
            .add(HIDDEN_INPUT_TAG_NAME, FRAGMENT_REDIRECT_IDENTIFIER));

    callActionKnotWithAssertions(context, vertx, knotContext,
        clientResponse -> {
          assertEquals(HttpResponseStatus.MOVED_PERMANENTLY.code(),
              clientResponse.getClientResponse().getStatusCode());
          assertEquals("/content/form/step2.html",
              clientResponse.getClientResponse().getHeaders().get("Location"));
          assertNull(clientResponse.getTransition());
          assertNull(clientResponse.getFragments());
        });
  }

  @Test
  @KnotxApplyConfiguration("knotx-test.json")
  public void callPostWithActionFragmentWithoutRequestedFragmentIdentifier_expectStatusCode500(
      VertxTestContext context, Vertx vertx) throws Exception {
    KnotContext knotContext = createKnotContext("fragment_form_incorrect_identifier_in.txt");
    knotContext.getClientRequest().setMethod(HttpMethod.POST);

    callActionKnotWithAssertions(context, vertx, knotContext,
        clientResponse -> {
          assertEquals(HttpResponseStatus.INTERNAL_SERVER_ERROR.code(),
              clientResponse.getClientResponse().getStatusCode());
          assertNull(clientResponse.getFragments());
          assertNull(clientResponse.getTransition());
        });
  }

  @Test
  @KnotxApplyConfiguration("knotx-test.json")
  public void callPostWithActionFragmentWithIncorrectSnippetId_expectStatusCode500(
      VertxTestContext context, Vertx vertx) throws Exception {
    KnotContext knotContext = createKnotContext("fragment_form_redirect_in.txt");
    knotContext.getClientRequest().setMethod(HttpMethod.POST)
        .setFormAttributes(
            MultiMap.caseInsensitiveMultiMap().add(HIDDEN_INPUT_TAG_NAME, "snippet_id_not_exists"));

    callActionKnotWithAssertions(context, vertx, knotContext,
        clientResponse -> {
          assertEquals(HttpResponseStatus.INTERNAL_SERVER_ERROR.code(),
              clientResponse.getClientResponse().getStatusCode());
          assertNull(clientResponse.getFragments());
          assertNull(clientResponse.getTransition());
        });
  }

  private void callActionKnotWithAssertions(
      VertxTestContext context, Vertx vertx, KnotContext knotContext,
      Consumer<KnotContext> onSuccess) {
    KnotProxy actionKnot = KnotProxy.createProxy(vertx, ADDRESS);

    actionKnot.rxProcess(knotContext)
        .doOnSuccess(onSuccess)
        .subscribe(
            success -> context.completeNow(),
            context::failNow
        );
  }

  private KnotContext createKnotContext(String... snippetFilenames) throws Exception {
    return createKnotContext(null, null, snippetFilenames);
  }

  private KnotContext createKnotContext(Fragment firstFragment, Fragment lastFragment,
      String... snippetFilenames) throws Exception {
    List<Fragment> fragments = Lists.newArrayList();
    Optional.ofNullable(firstFragment).ifPresent(fragments::add);
    for (String file : snippetFilenames) {
      String fileContent = KnotxTestUtils.readText(file);
      String fragmentIdentifiers = Jsoup.parse(fileContent).getElementsByAttribute(FRAGMENT_KNOTS)
          .attr(
              FRAGMENT_KNOTS);
      fragments.add(Fragment.snippet(Arrays.asList(fragmentIdentifiers.split(",")), fileContent));
    }
    Optional.ofNullable(lastFragment).ifPresent(fragments::add);

    return KnotContextFactory.empty(fragments);
  }

  private String clean(String text) {
    String cleanText = text.replace("\n", "").replaceAll(">(\\s)+<", "><")
        .replaceAll(">(\\s)+\\{", ">{").replaceAll("\\}(\\s)+<", "}<");
    return Jsoup.parse(cleanText, "UTF-8", Parser.xmlParser())
        .outputSettings(OUTPUT_SETTINGS)
        .html()
        .trim();
  }

  private void createMockAdapter(Vertx vertx, String address, String addToBody, String signal) {
    createMockAdapter(vertx, address, addToBody, signal, Collections.emptyMap());
  }

  private void createMockAdapter(Vertx vertx, String address, String addToBody, String signal,
      Map<String, List<String>> headers) {
    Function<AdapterRequest, AdapterResponse> adapter = adapterRequest -> {
      ClientResponse response = new ClientResponse();
      response.setStatusCode(HttpResponseStatus.OK.code());
      response.setBody(Buffer.buffer().appendString(addToBody));
      response.setHeaders(
          headers.keySet().stream().collect(MultiMapCollector.toMultiMap(o -> o, headers::get)));
      return new AdapterResponse().setResponse(response).setSignal(signal);
    };

    new ServiceBinder(vertx.getDelegate())
        .setAddress(address)
        .register(AdapterProxy.class, new MockAdapterImpl(adapter));
  }

  private class MockAdapterImpl implements AdapterProxy {

    private final Function<AdapterRequest, AdapterResponse> adapter;

    private MockAdapterImpl(Function<AdapterRequest, AdapterResponse> adapter) {
      this.adapter = adapter;
    }

    @Override
    public void process(AdapterRequest request, Handler<AsyncResult<AdapterResponse>> result) {
      result.handle(Future.succeededFuture(adapter.apply(request)));
    }
  }

}
