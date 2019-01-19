/*
 * Copyright (C) 2018 Toshiaki Maki <makingx@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package am.ik.yavi.constraint.base;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

import static am.ik.yavi.constraint.ViolationMessage.Default.*;
import static am.ik.yavi.core.builder.NullAs.INVALID;
import static am.ik.yavi.core.builder.NullAs.VALID;

import am.ik.yavi.constraint.Constraint;
import am.ik.yavi.core.builder.ConstraintPredicate;
import am.ik.yavi.core.builder.ViolatedValue;

public abstract class ContainerConstraintBase<T, V, C extends Constraint<T, V, C>>
		extends ConstraintBase<T, V, C> {

	abstract protected ToIntFunction<V> size();

	public C notEmpty() {
		this.predicates()
				.add(ConstraintPredicate.of(x -> x != null && size().applyAsInt(x) != 0,
						CONTAINER_NOT_EMPTY, () -> new Object[] {}, INVALID));
		return cast();
	}

	protected Function<V, Optional<ViolatedValue>> checkSizePredicate(
			Predicate<V> predicate, ToIntFunction<V> size) {
		return v -> {
			if (predicate.test(v)) {
				return Optional.empty();
			}
			int s = size.applyAsInt(v);
			return Optional.of(new ViolatedValue(s));
		};
	}

	public C lessThan(int max) {
		this.predicates()
				.add(ConstraintPredicate.withViolatedValue(
						this.checkSizePredicate(x -> size().applyAsInt(x) < max,
								this.size()),
						CONTAINER_LESS_THAN, () -> new Object[] { max }, VALID));
		return cast();
	}

	public C lessThanOrEqual(int max) {
		this.predicates()
				.add(ConstraintPredicate.withViolatedValue(
						this.checkSizePredicate(x -> size().applyAsInt(x) <= max,
								this.size()),
						CONTAINER_LESS_THAN_OR_EQUAL, () -> new Object[] { max }, VALID));
		return cast();
	}

	public C greaterThan(int min) {
		this.predicates()
				.add(ConstraintPredicate.withViolatedValue(
						this.checkSizePredicate(x -> size().applyAsInt(x) > min,
								this.size()),
						CONTAINER_GREATER_THAN, () -> new Object[] { min }, VALID));
		return cast();
	}

	public C greaterThanOrEqual(int min) {
		this.predicates()
				.add(ConstraintPredicate.withViolatedValue(
						this.checkSizePredicate(x -> size().applyAsInt(x) >= min,
								this.size()),
						CONTAINER_GREATER_THAN_OR_EQUAL, () -> new Object[] { min },
						VALID));
		return cast();
	}

	public C fixedSize(int size) {
		this.predicates()
				.add(ConstraintPredicate.withViolatedValue(
						this.checkSizePredicate(x -> size().applyAsInt(x) == size,
								this.size()),
						CONTAINER_FIXED_SIZE, () -> new Object[] { size }, VALID));
		return cast();
	}
}
