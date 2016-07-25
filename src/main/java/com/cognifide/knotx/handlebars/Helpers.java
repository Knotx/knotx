package com.cognifide.knotx.handlebars;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

public enum Helpers implements Helper<Object> {

	/**
	 * Checks if current value and given parameter are equal as Strings.<br/>
	 * Usage:
	 * <p>
	 * <pre>
	 *     {{#string_equals value "parameter"}}
	 *         equal
	 *     {{else}}
	 *         not equal
	 *     {{/string_equals}}
	 * </pre>
	 * <p>
	 * If value is "parameter", the output will be "equal".
	 */
	STRING_EQUALS("string_equals") {
		@Override
		public CharSequence apply(Object value, Options options) throws IOException {
			Options.Buffer buffer = options.buffer();
			if (StringUtils.equals(String.valueOf(value), options.param(0, StringUtils.EMPTY))) {
				buffer.append(options.fn());
			} else {
				buffer.append(options.inverse());
			}
			return buffer;
		}
	},

	/**
	 * Encodes given value.<br/>
	 * Usage:
	 * <p>
	 * <pre>
	 *     {{encode_uri value}}
	 * </pre>
	 * <p>
	 * If value is "ex@mple string", the output will be "ex%40mple+string"
	 */
	ENCODE_URI("encode_uri") {
		@Override
		public CharSequence apply(Object value, Options options) throws IOException {
			Options.Buffer buffer = options.buffer();
			buffer.append(URLEncoder.encode(String.valueOf(value), StandardCharsets.UTF_8.name()));
			return buffer;
		}
	};

	private final String name;

	Helpers(String name) {
		this.name = name;
	}

	public static void registerHelpers(Handlebars handlebars) {
		Stream.of(values()).forEach(helper -> handlebars.registerHelper(helper.name, helper));
	}

	@Override
	public abstract CharSequence apply(Object context, Options options) throws IOException;
}
