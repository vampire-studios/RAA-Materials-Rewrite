package net.vampirestudios.raaMaterials;

import com.mojang.datafixers.util.*;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface StreamCodecExpanded {
	static <B, C, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> StreamCodec<B, C> composite(
			StreamCodec<? super B, T1> streamCodec,
			Function<C, T1> function,
			StreamCodec<? super B, T2> streamCodec2,
			Function<C, T2> function2,
			StreamCodec<? super B, T3> streamCodec3,
			Function<C, T3> function3,
			StreamCodec<? super B, T4> streamCodec4,
			Function<C, T4> function4,
			StreamCodec<? super B, T5> streamCodec5,
			Function<C, T5> function5,
			StreamCodec<? super B, T6> streamCodec6,
			Function<C, T6> function6,
			StreamCodec<? super B, T7> streamCodec7,
			Function<C, T7> function7,
			StreamCodec<? super B, T8> streamCodec8,
			Function<C, T8> function8,
			StreamCodec<? super B, T9> streamCodec9,
			Function<C, T9> function9,
			StreamCodec<? super B, T10> streamCodec10,
			Function<C, T10> function10,
			StreamCodec<? super B, T11> streamCodec11,
			Function<C, T11> function11,
			StreamCodec<? super B, T12> streamCodec12,
			Function<C, T12> function12,
			Function12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, C> function112
	) {
		return new StreamCodec<>() {
			@Override
			public @NotNull C decode(B object) {
				T1 object2 = streamCodec.decode(object);
				T2 object3 = streamCodec2.decode(object);
				T3 object4 = streamCodec3.decode(object);
				T4 object5 = streamCodec4.decode(object);
				T5 object6 = streamCodec5.decode(object);
				T6 object7 = streamCodec6.decode(object);
				T7 object8 = streamCodec7.decode(object);
				T8 object9 = streamCodec8.decode(object);
				T9 object10 = streamCodec9.decode(object);
				T10 object11 = streamCodec10.decode(object);
				T11 object12 = streamCodec11.decode(object);
				T12 object13 = streamCodec12.decode(object);
				return function112.apply(object2, object3, object4, object5, object6, object7, object8, object9, object10, object11,
						object12, object13);
			}

			@Override
			public void encode(B object, C object2) {
				streamCodec.encode(object, function.apply(object2));
				streamCodec2.encode(object, function2.apply(object2));
				streamCodec3.encode(object, function3.apply(object2));
				streamCodec4.encode(object, function4.apply(object2));
				streamCodec5.encode(object, function5.apply(object2));
				streamCodec6.encode(object, function6.apply(object2));
				streamCodec7.encode(object, function7.apply(object2));
				streamCodec8.encode(object, function8.apply(object2));
				streamCodec9.encode(object, function9.apply(object2));
				streamCodec10.encode(object, function10.apply(object2));
				streamCodec11.encode(object, function11.apply(object2));
				streamCodec12.encode(object, function12.apply(object2));
			}
		};
	}
	static <B, C, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> StreamCodec<B, C> composite(
			StreamCodec<? super B, T1> streamCodec,
			Function<C, T1> function,
			StreamCodec<? super B, T2> streamCodec2,
			Function<C, T2> function2,
			StreamCodec<? super B, T3> streamCodec3,
			Function<C, T3> function3,
			StreamCodec<? super B, T4> streamCodec4,
			Function<C, T4> function4,
			StreamCodec<? super B, T5> streamCodec5,
			Function<C, T5> function5,
			StreamCodec<? super B, T6> streamCodec6,
			Function<C, T6> function6,
			StreamCodec<? super B, T7> streamCodec7,
			Function<C, T7> function7,
			StreamCodec<? super B, T8> streamCodec8,
			Function<C, T8> function8,
			StreamCodec<? super B, T9> streamCodec9,
			Function<C, T9> function9,
			StreamCodec<? super B, T10> streamCodec10,
			Function<C, T10> function10,
			StreamCodec<? super B, T11> streamCodec11,
			Function<C, T11> function11,
			StreamCodec<? super B, T12> streamCodec12,
			Function<C, T12> function12,
			StreamCodec<? super B, T13> streamCodec13,
			Function<C, T13> function13,
			Function13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, C> function112
	) {
		return new StreamCodec<>() {
			@Override
			public @NotNull C decode(B object) {
				T1 object2 = streamCodec.decode(object);
				T2 object3 = streamCodec2.decode(object);
				T3 object4 = streamCodec3.decode(object);
				T4 object5 = streamCodec4.decode(object);
				T5 object6 = streamCodec5.decode(object);
				T6 object7 = streamCodec6.decode(object);
				T7 object8 = streamCodec7.decode(object);
				T8 object9 = streamCodec8.decode(object);
				T9 object10 = streamCodec9.decode(object);
				T10 object11 = streamCodec10.decode(object);
				T11 object12 = streamCodec11.decode(object);
				T12 object13 = streamCodec12.decode(object);
				T13 object14 = streamCodec13.decode(object);
				return function112.apply(object2, object3, object4, object5, object6, object7, object8, object9, object10, object11,
						object12, object13, object14);
			}

			@Override
			public void encode(B object, C object2) {
				streamCodec.encode(object, function.apply(object2));
				streamCodec2.encode(object, function2.apply(object2));
				streamCodec3.encode(object, function3.apply(object2));
				streamCodec4.encode(object, function4.apply(object2));
				streamCodec5.encode(object, function5.apply(object2));
				streamCodec6.encode(object, function6.apply(object2));
				streamCodec7.encode(object, function7.apply(object2));
				streamCodec8.encode(object, function8.apply(object2));
				streamCodec9.encode(object, function9.apply(object2));
				streamCodec10.encode(object, function10.apply(object2));
				streamCodec11.encode(object, function11.apply(object2));
				streamCodec12.encode(object, function12.apply(object2));
				streamCodec13.encode(object, function13.apply(object2));
			}
		};
	}
	static <B, C, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> StreamCodec<B, C> composite(
			StreamCodec<? super B, T1> streamCodec,
			Function<C, T1> function,
			StreamCodec<? super B, T2> streamCodec2,
			Function<C, T2> function2,
			StreamCodec<? super B, T3> streamCodec3,
			Function<C, T3> function3,
			StreamCodec<? super B, T4> streamCodec4,
			Function<C, T4> function4,
			StreamCodec<? super B, T5> streamCodec5,
			Function<C, T5> function5,
			StreamCodec<? super B, T6> streamCodec6,
			Function<C, T6> function6,
			StreamCodec<? super B, T7> streamCodec7,
			Function<C, T7> function7,
			StreamCodec<? super B, T8> streamCodec8,
			Function<C, T8> function8,
			StreamCodec<? super B, T9> streamCodec9,
			Function<C, T9> function9,
			StreamCodec<? super B, T10> streamCodec10,
			Function<C, T10> function10,
			StreamCodec<? super B, T11> streamCodec11,
			Function<C, T11> function11,
			StreamCodec<? super B, T12> streamCodec12,
			Function<C, T12> function12,
			StreamCodec<? super B, T13> streamCodec13,
			Function<C, T13> function13,
			StreamCodec<? super B, T14> streamCodec14,
			Function<C, T14> function14,
			Function14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, C>function112
	) {
		return new StreamCodec<>() {
			@Override
			public @NotNull C decode(B object) {
				T1 object2 = streamCodec.decode(object);
				T2 object3 = streamCodec2.decode(object);
				T3 object4 = streamCodec3.decode(object);
				T4 object5 = streamCodec4.decode(object);
				T5 object6 = streamCodec5.decode(object);
				T6 object7 = streamCodec6.decode(object);
				T7 object8 = streamCodec7.decode(object);
				T8 object9 = streamCodec8.decode(object);
				T9 object10 = streamCodec9.decode(object);
				T10 object11 = streamCodec10.decode(object);
				T11 object12 = streamCodec11.decode(object);
				T12 object13 = streamCodec12.decode(object);
				T13 object14 = streamCodec13.decode(object);
				T14 object15 = streamCodec14.decode(object);
				return function112.apply(object2, object3, object4, object5, object6, object7, object8, object9, object10, object11,
						object12, object13, object14, object15);
			}

			@Override
			public void encode(B object, C object2) {
				streamCodec.encode(object, function.apply(object2));
				streamCodec2.encode(object, function2.apply(object2));
				streamCodec3.encode(object, function3.apply(object2));
				streamCodec4.encode(object, function4.apply(object2));
				streamCodec5.encode(object, function5.apply(object2));
				streamCodec6.encode(object, function6.apply(object2));
				streamCodec7.encode(object, function7.apply(object2));
				streamCodec8.encode(object, function8.apply(object2));
				streamCodec9.encode(object, function9.apply(object2));
				streamCodec10.encode(object, function10.apply(object2));
				streamCodec11.encode(object, function11.apply(object2));
				streamCodec12.encode(object, function12.apply(object2));
				streamCodec13.encode(object, function13.apply(object2));
				streamCodec14.encode(object, function14.apply(object2));
			}
		};
	}
	static <B, C, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> StreamCodec<B, C> composite(
			StreamCodec<? super B, T1> streamCodec,
			Function<C, T1> function,
			StreamCodec<? super B, T2> streamCodec2,
			Function<C, T2> function2,
			StreamCodec<? super B, T3> streamCodec3,
			Function<C, T3> function3,
			StreamCodec<? super B, T4> streamCodec4,
			Function<C, T4> function4,
			StreamCodec<? super B, T5> streamCodec5,
			Function<C, T5> function5,
			StreamCodec<? super B, T6> streamCodec6,
			Function<C, T6> function6,
			StreamCodec<? super B, T7> streamCodec7,
			Function<C, T7> function7,
			StreamCodec<? super B, T8> streamCodec8,
			Function<C, T8> function8,
			StreamCodec<? super B, T9> streamCodec9,
			Function<C, T9> function9,
			StreamCodec<? super B, T10> streamCodec10,
			Function<C, T10> function10,
			StreamCodec<? super B, T11> streamCodec11,
			Function<C, T11> function11,
			StreamCodec<? super B, T12> streamCodec12,
			Function<C, T12> function12,
			StreamCodec<? super B, T13> streamCodec13,
			Function<C, T13> function13,
			StreamCodec<? super B, T14> streamCodec14,
			Function<C, T14> function14,
			StreamCodec<? super B, T15> streamCodec15,
			Function<C, T15> function15,
			Function15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, C> function112
	) {
		return new StreamCodec<>() {
			@Override
			public @NotNull C decode(B object) {
				T1 object2 = streamCodec.decode(object);
				T2 object3 = streamCodec2.decode(object);
				T3 object4 = streamCodec3.decode(object);
				T4 object5 = streamCodec4.decode(object);
				T5 object6 = streamCodec5.decode(object);
				T6 object7 = streamCodec6.decode(object);
				T7 object8 = streamCodec7.decode(object);
				T8 object9 = streamCodec8.decode(object);
				T9 object10 = streamCodec9.decode(object);
				T10 object11 = streamCodec10.decode(object);
				T11 object12 = streamCodec11.decode(object);
				T12 object13 = streamCodec12.decode(object);
				T13 object14 = streamCodec13.decode(object);
				T14 object15 = streamCodec14.decode(object);
				T15 object16 = streamCodec15.decode(object);
				return function112.apply(object2, object3, object4, object5, object6, object7, object8, object9, object10, object11,
						object12, object13, object14, object15, object16);
			}

			@Override
			public void encode(B object, C object2) {
				streamCodec.encode(object, function.apply(object2));
				streamCodec2.encode(object, function2.apply(object2));
				streamCodec3.encode(object, function3.apply(object2));
				streamCodec4.encode(object, function4.apply(object2));
				streamCodec5.encode(object, function5.apply(object2));
				streamCodec6.encode(object, function6.apply(object2));
				streamCodec7.encode(object, function7.apply(object2));
				streamCodec8.encode(object, function8.apply(object2));
				streamCodec9.encode(object, function9.apply(object2));
				streamCodec10.encode(object, function10.apply(object2));
				streamCodec11.encode(object, function11.apply(object2));
				streamCodec12.encode(object, function12.apply(object2));
				streamCodec13.encode(object, function13.apply(object2));
				streamCodec14.encode(object, function14.apply(object2));
				streamCodec15.encode(object, function15.apply(object2));
			}
		};
	}
	static <B, C, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> StreamCodec<B, C> composite(
			StreamCodec<? super B, T1> streamCodec,
			Function<C, T1> function,
			StreamCodec<? super B, T2> streamCodec2,
			Function<C, T2> function2,
			StreamCodec<? super B, T3> streamCodec3,
			Function<C, T3> function3,
			StreamCodec<? super B, T4> streamCodec4,
			Function<C, T4> function4,
			StreamCodec<? super B, T5> streamCodec5,
			Function<C, T5> function5,
			StreamCodec<? super B, T6> streamCodec6,
			Function<C, T6> function6,
			StreamCodec<? super B, T7> streamCodec7,
			Function<C, T7> function7,
			StreamCodec<? super B, T8> streamCodec8,
			Function<C, T8> function8,
			StreamCodec<? super B, T9> streamCodec9,
			Function<C, T9> function9,
			StreamCodec<? super B, T10> streamCodec10,
			Function<C, T10> function10,
			StreamCodec<? super B, T11> streamCodec11,
			Function<C, T11> function11,
			StreamCodec<? super B, T12> streamCodec12,
			Function<C, T12> function12,
			StreamCodec<? super B, T13> streamCodec13,
			Function<C, T13> function13,
			StreamCodec<? super B, T14> streamCodec14,
			Function<C, T14> function14,
			StreamCodec<? super B, T15> streamCodec15,
			Function<C, T15> function15,
			StreamCodec<? super B, T16> streamCodec16,
			Function<C, T16> function16,
			Function16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, C> function112
	) {
		return new StreamCodec<>() {
			@Override
			public @NotNull C decode(B object) {
				T1 object2 = streamCodec.decode(object);
				T2 object3 = streamCodec2.decode(object);
				T3 object4 = streamCodec3.decode(object);
				T4 object5 = streamCodec4.decode(object);
				T5 object6 = streamCodec5.decode(object);
				T6 object7 = streamCodec6.decode(object);
				T7 object8 = streamCodec7.decode(object);
				T8 object9 = streamCodec8.decode(object);
				T9 object10 = streamCodec9.decode(object);
				T10 object11 = streamCodec10.decode(object);
				T11 object12 = streamCodec11.decode(object);
				T12 object13 = streamCodec12.decode(object);
				T13 object14 = streamCodec13.decode(object);
				T14 object15 = streamCodec14.decode(object);
				T15 object16 = streamCodec15.decode(object);
				T16 object17 = streamCodec16.decode(object);
				return function112.apply(object2, object3, object4, object5, object6, object7, object8, object9, object10, object11,
						object12, object13, object14, object15, object16, object17);
			}

			@Override
			public void encode(B object, C object2) {
				streamCodec.encode(object, function.apply(object2));
				streamCodec2.encode(object, function2.apply(object2));
				streamCodec3.encode(object, function3.apply(object2));
				streamCodec4.encode(object, function4.apply(object2));
				streamCodec5.encode(object, function5.apply(object2));
				streamCodec6.encode(object, function6.apply(object2));
				streamCodec7.encode(object, function7.apply(object2));
				streamCodec8.encode(object, function8.apply(object2));
				streamCodec9.encode(object, function9.apply(object2));
				streamCodec10.encode(object, function10.apply(object2));
				streamCodec11.encode(object, function11.apply(object2));
				streamCodec12.encode(object, function12.apply(object2));
				streamCodec13.encode(object, function13.apply(object2));
				streamCodec14.encode(object, function14.apply(object2));
				streamCodec15.encode(object, function15.apply(object2));
				streamCodec16.encode(object, function16.apply(object2));
			}
		};
	}
}
