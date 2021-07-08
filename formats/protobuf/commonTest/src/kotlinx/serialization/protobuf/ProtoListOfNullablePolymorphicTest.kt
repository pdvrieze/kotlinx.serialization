/*
 * Copyright 2017-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.serialization.protobuf

import kotlinx.serialization.*
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.modules.*
import kotlin.test.*

class ProtoListOfNullablePolymorphicTest {

    @Serializable
    data class Nulls(val poly: List<@Polymorphic Any?>, val bar: Boolean)

    private val module = SerializersModule {
        polymorphic(Any::class, String::class, String.serializer())
    }

    private val protobuf = ProtoBuf {
        serializersModule = module
    }

    @Test
    fun testEncodeProtobufListOfNullablePolymorphic() {
        assertEquals(HEX_NOT_NULL, protobuf.encodeToHexString(Nulls(listOf("1", "2", "3"), true)))
        val ex = assertFailsWith<SerializationException> {
            protobuf.encodeToHexString(Nulls(listOf(null), false))
        }
        assertEquals("'null' is not supported for collection elements in ProtoBuf", ex.message)
    }

    @Test
    fun testDecodeProtobufListOfNullablePolymorphic() {
        assertEquals(Nulls(listOf("1", "2", "3"), true), protobuf.decodeFromHexString(HEX_NOT_NULL))
        assertEquals(Nulls(emptyList(), false), protobuf.decodeFromHexString(HEX_NULL))
    }

    companion object {
        const val HEX_NOT_NULL = "0a120a0d6b6f746c696e2e537472696e671201310a120a0d6b6f746c696e2e537472696e671201320a120a0d6b6f746c696e2e537472696e671201331001"
        const val HEX_NULL = "1000"
    }
}
