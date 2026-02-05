package com.barostartbe.global.config

import com.oracle.bmc.auth.SimpleAuthenticationDetailsProvider
import com.oracle.bmc.objectstorage.ObjectStorage
import com.oracle.bmc.objectstorage.ObjectStorageClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.FileInputStream
import java.io.InputStream
import java.util.function.Supplier

@Configuration
class OciConfig(
    @Value("\${oci.tenant-id}")
    private val tenantId: String,

    @Value("\${oci.user-id}")
    private val userId: String,

    @Value("\${oci.fingerprint}")
    private val fingerprint: String,

    @Value("\${oci.private-key-path}")
    private val privateKeyPath: String,

    @Value("\${oci.region}")
    private val region: String,

    @Value("\${oci.bucket.namespace}")
    private val bucketNamespace: String
) {

    @Bean
    fun objectStorageClient(): ObjectStorage =
        createStorageClient(createAuthenticationProvider())
            .also { client ->
                client.endpoint = "https://$bucketNamespace.objectstorage.$region.oci.customer-oci.com"
            }

    private fun createAuthenticationProvider(): SimpleAuthenticationDetailsProvider =
        SimpleAuthenticationDetailsProvider.builder().apply {
            tenantId(tenantId)
            userId(userId)
            fingerprint(fingerprint)
            privateKeySupplier(createPrivateKeySupplier())
        }.build()

    private fun createPrivateKeySupplier(): Supplier<InputStream> =
        Supplier { FileInputStream(privateKeyPath) }

    private fun createStorageClient(provider: SimpleAuthenticationDetailsProvider): ObjectStorage =
        ObjectStorageClient.builder().build(provider)
}
