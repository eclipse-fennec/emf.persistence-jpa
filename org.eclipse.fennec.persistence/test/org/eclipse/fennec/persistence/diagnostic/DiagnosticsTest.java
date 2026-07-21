/********************************************************************
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Data In Motion Consulting - initial implementation
 ********************************************************************/
package org.eclipse.fennec.persistence.diagnostic;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link Diagnostics} JUL bridge.
 */
class DiagnosticsTest {

	private static final class CapturingHandler extends Handler {
		private final List<LogRecord> records = new ArrayList<>();

		@Override
		public void publish(LogRecord record) {
			records.add(record);
		}

		@Override
		public void flush() {
		}

		@Override
		public void close() {
		}
	}

	private Logger newLogger(CapturingHandler handler) {
		Logger logger = Logger.getAnonymousLogger();
		logger.setUseParentHandlers(false);
		logger.setLevel(Level.ALL);
		logger.addHandler(handler);
		return logger;
	}

	@Test
	void severitiesMapToJulLevels() {
		assertThat(Diagnostics.toLevel(Diagnostic.OK)).isEqualTo(Level.INFO);
		assertThat(Diagnostics.toLevel(Diagnostic.INFO)).isEqualTo(Level.INFO);
		assertThat(Diagnostics.toLevel(Diagnostic.WARNING)).isEqualTo(Level.WARNING);
		assertThat(Diagnostics.toLevel(Diagnostic.ERROR)).isEqualTo(Level.SEVERE);
		assertThat(Diagnostics.toLevel(Diagnostic.CANCEL)).isEqualTo(Level.SEVERE);
	}

	@Test
	void logWritesMessageLevelAndException() {
		CapturingHandler handler = new CapturingHandler();
		Logger logger = newLogger(handler);
		Exception cause = new IllegalStateException("boom");

		Diagnostics.log(logger, List.of(
				new BasicDiagnostic(Diagnostic.WARNING, "src", 0, "watch out", null),
				new BasicDiagnostic(Diagnostic.ERROR, "src", 0, "broken", new Object[] { cause })));

		assertThat(handler.records).hasSize(2);
		assertThat(handler.records.get(0).getLevel()).isEqualTo(Level.WARNING);
		assertThat(handler.records.get(0).getMessage()).isEqualTo("watch out");
		assertThat(handler.records.get(0).getThrown()).isNull();
		assertThat(handler.records.get(1).getLevel()).isEqualTo(Level.SEVERE);
		assertThat(handler.records.get(1).getThrown()).isSameAs(cause);
	}
}
