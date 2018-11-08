# Fallback Handling

Sometimes during Fragment processing things can go wrong. There can be multiple reasons -misconfiguration, programming 
error, lack of service availability. KnotX gives you the ability to define a fallback strategy that is applied when 
dynamic fragment processing fails.

## How does it work? 

When no fallback is defined an error that occurs during processing of a single fragment will trigger a 500 error 
response from the server. This will basically stop the page rendering process. 

You have the option to alter this behavior. Knots have the option to mark Fragments with enabled fallback as 
failed - and continue the rendering process. 

Failed fragments are ignored by [[HandlebarsKnot]]. 

Failed fragments are not rendered by the [[Assembler]]. Instead of rendering content of the failed fragment 
[[Assembler]] will apply defined fallback strategy. 
- you may use `BLANK` fallback strategy - in such case failed snippets will not be rendered at all.
- you may provide your own markup that will replace the failed snippet
- you may configure this behavior for each snippet or globally           

## How to configure? 

You may configure fallback behavior for each snippet - or you may use global configuration to apply fallback to all 
snippets in your application. Local configuration (on snippet level) takes precedence over the global one. 
    
### Simple blank fallback (per snippet)
To render an empty string instead of your failed snippet add `data-knotx-fallback="BLANK"` attribute:
```
<knotx-snippet data-knotx-knots="databridge,handlebars" 
  data-knotx-databridge-name="unstable-service"
  data-knotx-fallback="BLANK">
      {{#if _result}}<h2>{{_result.count}}</h2>{{/if}}
</knotx-snippet>
```
### Static markup fallback (per snippet)
To render an static markup instead of your failed snippet: 
1. create a fallback snippet with `data-knotx-fallback-id` attribute 
2. point to it in your snippet
```
<knotx-snippet data-knotx-knots="databridge,handlebars" 
  data-knotx-databridge-name="unstable-service"
  data-knotx-fallback="CUSTOM">
      {{#if _result}}<h2>{{_result.count}}</h2>{{/if}}
</knotx-snippet>
<knotx:fallback data-knotx-fallback-id="CUSTOM">
  <p class="error">error</p>
</knotx:fallback>
```
### Global blank fallback
to apply BLANK fallback to all snippets by default configure `defaultFallback` attribute within `snippetOptions` - 
apply these to both Splitter and FragmentAssembler. 
```
  snippetOptions {
    tagName = knotx-snippet
    paramsPrefix = data-knotx-
    defaultFallback = BLANK
  }
```
### Global fallback with custom markup
to apply custom markup fallback to all snippets by default: 
- configure `defaultFallback` attribute within `snippetOptions` 
- define your own `fallbacks` property within `snippetOptions`
- apply these to both Splitter and FragmentAssembler. 
```
  snippetOptions {
    tagName = knotx-snippet
    paramsPrefix = data-knotx-
    defaultFallback = CUSTOM_GLOBAL
    fallbacks = [
      {
        id = CUSTOM_GLOBAL
        markup = "<knotx:fallback data-knotx-fallback-id='CUSTOM_GLOBAL'><p class="error">error</p></knotx:fallback>"
      }
    ]
  }
```

## How to extend? 
you can deliver your own class that will be used by the assembler to process failed Fragment. You need to:   
 
- implement FallbackStrategy interface
```java
import io.knotx.dataobjects.Fragment;
import io.knotx.dataobjects.KnotContext;
import io.knotx.fallback.FallbackStrategy;

public class MyFallbackStrategy implements FallbackStrategy {

  @Override
  public String getId() {
    return "CUSTOM_STRATEGY";
  }

  @Override
  public String applyFallback(Fragment failed, Fragment fallback, KnotContext knotContext) {
    return "<p>markup</p>";
  }
}
```
- make sure that `ServiceLoader` can discover this class - add its fully qualified name to 
`/META-INF/services/io.knotx.fallback.FallbackStrategy` file. 
- create a custom fallback snippet with linked strategy Id. Use `data-knotx-fallback-strategy` attribute to provide 
the strategy id (as defined in your java class)  
```
<knotx-snippet data-knotx-knots="databridge,handlebars" 
  data-knotx-databridge-name="unstable-service"
  data-knotx-fallback="CUSTOM">
      {{#if _result}}<h2>{{_result.count}}</h2>{{/if}}
</knotx-snippet>
<knotx:fallback data-knotx-fallback-id="CUSTOM" data-knotx-fallback-strategy="CUSTOM_STRATEGY">
  <p class="error">error</p>
</knotx:fallback>
```

### Implementation guidelines, marking nodes as failed
If you are writing your own Knots that are processing individual fragments you should follow these guidelines. 

To indicate that a single fragment processing has failed you should call `faliure` method. You must provide name of the 
knot that encountered the failure and the exception.

```java
public void fragmentFailure(Fragment fragment, String knotId, Throwable t) {
  fragment.failure(knotId, t);
}
```
If your knot does Fragment processing it should skip Fragments that are already marked as failed. If you extend the 
`AbstractKnotProxy` class you should use method `boolean shouldProcess(Fragment fragment)` to check if given Fragment 
should be processed.  
