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
package io.knotx.example.monolith;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.knotx.dataobjects.AdapterResponse;
import io.knotx.dataobjects.ClientResponse;
import io.knotx.junit.util.FileReader;
import io.knotx.junit5.KnotxApplyConfiguration;
import io.knotx.junit5.KnotxExtension;
import io.knotx.junit5.KnotxTestUtils;
import io.knotx.proxy.AdapterProxy;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.Observable;
import io.vertx.core.Future;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.http.HttpClient;
import io.vertx.reactivex.core.http.HttpClientResponse;
import io.vertx.serviceproxy.ServiceBinder;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(KnotxExtension.class)
public class SampleApplicationIT {

  private static final String REMOTE_REQUEST_URI = "/content/remote/simple.html";
  private static final String REMOTE_REQUEST_URI_WITH_PARAMETER_CONTAINING_SPACE = "/content/remote/simple.html?parameter%20with%20space=value";
  private static final String LOCAL_REQUEST_URI = "/content/local/simple.html";
  private static final String MISSING_SERVICE_CONFIG_REQUEST_URI = "/content/local/missingServiceConfig.html";
  private static final String LOCAL_NO_BODY_REQUEST_URI = "/content/local/noBody.html";
  private static final String LOCAL_MULTIPLE_FORMS_URI = "/content/local/multiple-forms.html";
  private static final int KNOTX_SERVER_PORT = 9092;
  private static final String KNOTX_SERVER_ADDRESS = "localhost";

  @Test
  @KnotxApplyConfiguration("knotx-test-app.json")
  public void whenRequestingLocalSimplePageWithGet_expectLocalSimpleHtml(
      VertxTestContext context, Vertx vertx) {
    testGetRequest(context, vertx, LOCAL_REQUEST_URI, "localSimpleResult.html");
  }

  @Test
  @KnotxApplyConfiguration("knotx-test-app-custom-symbol.json")
  public void whenRequestingLocalSimplePageWithGetCustomSymbol_expectLocalSimpleHtml(
      VertxTestContext context, Vertx vertx) {
    testGetRequest(context, vertx, "/content/local/customSymbol.html", "localSimpleResultAngular.html");
  }

  @Test
  @KnotxApplyConfiguration("knotx-test-app-custom-and-default-symbol.json")
  public void whenRequestingLocalSimplePageWithGetCustomAndDefaultSymbol_expectLocalSimpleHtmlWithDefault(
      VertxTestContext context, Vertx vertx) {
    testGetRequest(context, vertx, "/content/local/customAndDefaultSymbol.html",
        "localSimpleResultCustomAndDefault.html");
  }

  @Test
  @KnotxApplyConfiguration("knotx-test-app-no-body.json")
  public void whenRequestingLocalPageWhereInServiceIsMissingResponseBody_expectNoBodyHtml(
      VertxTestContext context, Vertx vertx) {
    testGetRequest(context, vertx, LOCAL_NO_BODY_REQUEST_URI, "noBody.html");
  }

  @Test
  @KnotxApplyConfiguration("knotx-test-app.json")
  public void whenRequestingPageWithMissingServiceWithoutConfiguration_expectServerError(
      VertxTestContext context, Vertx vertx) {
    testGetServerError(context, vertx, MISSING_SERVICE_CONFIG_REQUEST_URI);
  }

  @Test
  @KnotxApplyConfiguration("knotx-test-app.json")
  public void whenRequestingRemoteSimplePageWithGet_expectRemoteSimpleHtml(
      VertxTestContext context, Vertx vertx) {
    testGetRequest(context, vertx, REMOTE_REQUEST_URI, "remoteSimpleResult.html");
  }

  @Test
  @KnotxApplyConfiguration("knotx-test-app.json")
  public void whenRequestingRemoteSimplePageWithGetAndRequestParameterNameContainsSpace_expectRemoteSimpleHtml(
      VertxTestContext context, Vertx vertx) {
    testGetRequest(context, vertx, REMOTE_REQUEST_URI_WITH_PARAMETER_CONTAINING_SPACE,
        "remoteSimpleResult.html");
  }

  @Test
  @KnotxApplyConfiguration("knotx-test-app.json")
  public void whenRequestingLocalMultipleFormsPageWithGet_expectMutlipleFormsWithGetResultHtml(
      VertxTestContext context, Vertx vertx) {
    testGetRequest(context, vertx, LOCAL_MULTIPLE_FORMS_URI, "multipleFormWithGetResult.html");
  }

  @Test
  @KnotxApplyConfiguration("knotx-test-app.json")
  public void whenRequestingWithPostMethodFirstForm_expectFirstFormPresentingFormActionResult(
      VertxTestContext context, Vertx vertx) {
    mockActionAdapter(vertx, getFirstTestFormData(), null);
    testPostRequest(context, vertx, LOCAL_MULTIPLE_FORMS_URI, getFirstTestFormData().getMap(),
        "multipleFormWithPostResult.html", false);
  }

  @Test
  @KnotxApplyConfiguration("knotx-test-app.json")
  public void whenRequestingWithPostFirstFormTwiceWithDifferentData_expectDifferentResultOfFirstFormForEachRequest(
      VertxTestContext context, Vertx vertx) {
    mockActionAdapter(vertx, getFirstTestFormData(), getSecondTestFormData());
    testPostRequest(context, vertx, LOCAL_MULTIPLE_FORMS_URI, getFirstTestFormData().getMap(),
        "multipleFormWithPostResult.html", false);
    testPostRequest(context, vertx, LOCAL_MULTIPLE_FORMS_URI, getSecondTestFormData().getMap(),
        "multipleFormWithPostResult2.html", false);
  }

  private void testPostRequest(VertxTestContext context, Vertx vertx, String url, Map<String, Object> formData,
      String expectedResponseFile, boolean ajaxCall) {
    HttpClient client = vertx.createHttpClient();

    Observable<HttpClientResponse> request = KnotxTestUtils
        .asyncRequest(client, HttpMethod.POST, KNOTX_SERVER_PORT,
        KNOTX_SERVER_ADDRESS, url, req -> {
          String bodyForm = formData.entrySet().stream()
              .map(entry -> entry.getKey() + "=" + entry.getValue())
              .reduce((p1, p2) -> p1 + "&" + p2).get();
          req.headers().set("content-length", String.valueOf(bodyForm.length()));
          req.headers().set("content-type", "application/x-www-form-urlencoded");
          if (ajaxCall) {
            req.headers().set("X-Requested-With", "XMLHttpRequest");
          }
          req.write(bodyForm);
        });

    request.subscribe(resp -> resp.bodyHandler(body -> {
      assertEquals(resp.statusCode(), HttpResponseStatus.OK.code());
      try {
        assertEquals(Jsoup.parse(body.toString()).body().html(),
            Jsoup.parse(FileReader.readText(expectedResponseFile)).body().html());
      } catch (Exception e) {
        context.failNow(e);
      }

      context.completeNow();
    }));
  }

  private void testGetRequest(VertxTestContext context, Vertx vertx, String url, String expectedResponseFile) {
    HttpClient client = vertx.createHttpClient(new HttpClientOptions());

    client.getNow(KNOTX_SERVER_PORT, KNOTX_SERVER_ADDRESS, url,
        resp -> resp.bodyHandler(body -> {
          assertEquals(resp.statusCode(), HttpResponseStatus.OK.code());
          try {
            assertEquals(Jsoup.parse(body.toString()).body().html().trim(),
                Jsoup.parse(FileReader.readText(expectedResponseFile)).body().html().trim());
          } catch (Exception e) {
            context.failNow(e);
          }
          client.close();
          context.completeNow();
        }));
  }

  private void testGetServerError(VertxTestContext context, Vertx vertx, String url) {
    HttpClient client = vertx.createHttpClient();
    client.getNow(KNOTX_SERVER_PORT, KNOTX_SERVER_ADDRESS, url,
        resp -> resp.bodyHandler(body -> {
          assertEquals(resp.statusCode(), HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
          client.close();
          context.completeNow();
        }));
  }

  private JsonObject getFirstTestFormData() {
    return new JsonObject()
        .put("name", "test")
        .put("email", "email-1@example.com")
        .put("_frmId", "competition");
  }

  private JsonObject getSecondTestFormData() {
    return new JsonObject()
        .put("name", "test")
        .put("email", "email-2@example.com")
        .put("_frmId", "newsletter");
  }

  private void mockActionAdapter(Vertx vertx, JsonObject competitionData,
      JsonObject newsletterData) {
    ClientResponse clientResponse = new ClientResponse().setStatusCode(404);
    AdapterResponse resp = new AdapterResponse().setResponse(clientResponse);

    new ServiceBinder(vertx.getDelegate())
        .setAddress("mock.action.adapter")
        .register(AdapterProxy.class, (request, result) -> {
          String path = request.getParams().getString("path");
          if (StringUtils.isNotBlank(path)) {
            if (path.equals("/service/mock/post-competition.json")) {
              clientResponse.setStatusCode(200)
                  .setBody(new JsonObject().put("form", competitionData).toBuffer());
            } else if (path.equals("/service/mock/post-newsletter.json")) {
              clientResponse.setStatusCode(200)
                  .setBody(new JsonObject().put("form", newsletterData).toBuffer());
            }
          }
          result.handle(Future.succeededFuture(resp));
        });
  }
}
