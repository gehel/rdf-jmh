/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.wikimedia.query.rdf;

import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Mode.AverageTime;
import static org.wikidata.query.rdf.test.StatementHelper.statement;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.LiteralImpl;
import org.wikidata.query.rdf.common.uri.SchemaDotOrg;

public class MyBenchmark {

    @State(Scope.Thread)
    public static class StatementState {
        public Statement statement = createStatement();
    }

    @Benchmark
    @BenchmarkMode(AverageTime) @OutputTimeUnit(NANOSECONDS)
    public void noCacheStatement(Blackhole blackhole, StatementState state) {
        for (int i = 0; i < 15; i++) {
            blackhole.consume(state.statement.getSubject().stringValue());
            blackhole.consume(state.statement.getObject().stringValue());
            blackhole.consume(state.statement.getPredicate().stringValue());
        }
    }

    @Benchmark
    @BenchmarkMode(AverageTime) @OutputTimeUnit(NANOSECONDS)
    public void objectCachedStatement(Blackhole blackhole, StatementState state) {
        CachedStatement statement = new CachedStatement(state.statement);
        for (int i = 0; i < 15; i++) {
            blackhole.consume(statement.subject);
            blackhole.consume(statement.object);
            blackhole.consume(statement.predicate);
        }
    }

    @Benchmark
    @BenchmarkMode(AverageTime) @OutputTimeUnit(NANOSECONDS)
    public void stringCachedStatement(Blackhole blackhole, StatementState state) {
        String subject = state.statement.getSubject().stringValue();
        String object = state.statement.getObject().stringValue();
        String predicate = state.statement.getPredicate().stringValue();
        for (int i = 0; i < 15; i++) {
            blackhole.consume(subject);
            blackhole.consume(object);
            blackhole.consume(predicate);
        }
    }

    private static Statement createStatement() {
        return statement("Q23", SchemaDotOrg.VERSION, new LiteralImpl("833473046"));
    }

    private static final class CachedStatement {
        final String subject;
        final String object;
        final String predicate;

        private CachedStatement(Statement statement) {
            this.subject = statement.getSubject().stringValue();
            this.object = statement.getObject().stringValue();
            this.predicate = statement.getPredicate().stringValue();
        }
    }

}
