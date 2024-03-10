package name.antonkonyshev.home.domain.repository

interface DiscoveryService {
    suspend fun discoverDevices()
}