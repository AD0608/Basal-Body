package com.basalbody.app.socket

import android.util.Log
import com.basalbody.app.BuildConfig
import com.basalbody.app.model.Resource
import com.basalbody.app.utils.Logger
import io.socket.client.Ack
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONObject

object SocketUtils {
    private const val TAG = "SocketUtils"
    private const val KEY_STATUS = "status"
    private const val KEY_DATA = "data"


    /**
     * Event Key-value
     */
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_TYPE = "user_type"
    private const val VALUE_USER_TYPE = "driver"
    private const val VALUE_RECEIVER_TYPE_FOR_COMPANY_DRIVER = "company"
    private const val VALUE_RECEIVER_TYPE_FOR_FREELANCER = "user"
    private const val KEY_DRIVER_ID = "driver_id"
    private const val KEY_RECEIVER_ID = "receiver_id"
    private const val KEY_RECEIVER_TYPE = "receiver_type"
    private const val KEY_LONGITUDE = "longitude"
    private const val KEY_LATITUDE = "latitude"
    private const val KEY_MESSAGE = "message"
    private const val KEY_SENDER_ID = "sender_id"
    private const val KEY_SENDER_TYPE = "sender_type"
    private const val KEY_SHIPMENT_ID = "shipment_id"

    enum class Events(val type: String) {
        EVENT_SEND_MESSAGE("send_message"),
        EVENT_JOIN_DRIVER("join_driver"),
        EVENT_UPDATE_LOCATION("update_location"),
        EVENT_LIVE_TRACKING("live_tracking")
    }

    var mSocket: Socket? = null
    //val socketDataLiveData = MutableLiveData<SocketChatData>()

    fun startSocket(onConnect: () -> Unit) {
        if (mSocket == null) {
            try {
                val options = IO.Options.builder()
                    .setReconnection(true)
                    .setReconnectionAttempts(Int.MAX_VALUE)
                    .setReconnectionDelay(1000)
                    .build()
                mSocket = IO.socket(
                    BuildConfig.SOCKET_BASE_URL,
                    options
                )
            } catch (e: Exception) {
                Log.d(TAG, "startSocket: ${e.message}")
            }
        }

        mSocket?.apply {
            if (!connected()) {
                registerDefaultEvents(onConnect)
                connect()
            } else onConnect()
        }
    }

    private fun registerDefaultEvents(onConnect: () -> Unit) {
        mSocket?.on(Socket.EVENT_CONNECT) {
            Logger.e(TAG, "Connected")
            onConnect()
        }
        mSocket?.on(Socket.EVENT_DISCONNECT) { Logger.e(TAG, "Disconnected") }
        mSocket?.on(Socket.EVENT_CONNECT_ERROR) { Logger.e(TAG, "Connect error") }
    }

    fun disconnectSocket() {
        mSocket?.disconnect()
        mSocket?.off()
        mSocket?.close()
        mSocket = null
    }

    fun joinSocket(userId: Int?) {
        val jsonObject = JSONObject().apply {
            put(KEY_USER_ID, userId ?: 0)
            put(KEY_USER_TYPE, VALUE_USER_TYPE)
        }
        Logger.e("params : $jsonObject")
        mSocket?.emit(Events.EVENT_JOIN_DRIVER.type, jsonObject, Ack { args ->
            Logger.e(TAG, "${Events.EVENT_JOIN_DRIVER.type}: ${args[0]}")
        })
    }

   /* fun emitSendMessage(
        message: String,
        shipmentId: Int,
        customer: Customer?,
        userId: Int,
        onSuccessEmit: ((ChatDetails?) -> Unit)? = null,
        isCompanyDriver: Boolean = false
    ) {
        val payload = JSONObject().apply {
            put(KEY_RECEIVER_ID, customer?.id)
            put(
                KEY_RECEIVER_TYPE,
                if (isCompanyDriver) VALUE_RECEIVER_TYPE_FOR_COMPANY_DRIVER else VALUE_RECEIVER_TYPE_FOR_FREELANCER
            )
            put(KEY_MESSAGE, message)
            put(KEY_SENDER_ID, userId)
            put(KEY_SENDER_TYPE, VALUE_USER_TYPE)
            put(KEY_SHIPMENT_ID, shipmentId)
        }
        Logger.e("params: $payload")
        mSocket?.emit(Events.EVENT_SEND_MESSAGE.type, payload, Ack { args ->
            val response = args[0].toString()
            Logger.e(TAG, "${Events.EVENT_SEND_MESSAGE.type}: response: $response")
            if (response.isNotEmpty()) {
                val jsonObject = JSONObject(response)
                val status = jsonObject.getBoolean(KEY_STATUS)
                if (status) {
                    val data = jsonObject.getJSONObject(KEY_DATA)
                    data.toString().toObject<ChatDetails>()?.let { chat ->
                        chat.isMe = true
                        chat.chatDateTime = chat.createdAt?.getDateTimeFromUTCDateTime(
                            inputFormat = TZ_ISO_FORMAT,
                            outputFormat = h_mm_a
                        ) ?: ""
                        onSuccessEmit?.invoke(chat)
                    }
                }
            }
        })
    }*/

    fun listenOnSendMessage() {
        mSocket?.takeIf { it.connected() }?.on(Events.EVENT_SEND_MESSAGE.type, sendMessageListener)
    }

    fun listenOffSendMessage() {
        mSocket?.takeIf { it.connected() }?.off(Events.EVENT_SEND_MESSAGE.type, sendMessageListener)
    }

    // Use sendMessageListener for SEND and RECEIVE messages
    val _callSendMessageMutableStateFlow =
        MutableStateFlow(Resource.Loading<Boolean>(isLoadingShow = false) as Resource<*>)
    val callSendMessageStateFlow: StateFlow<Resource<*>> = _callSendMessageMutableStateFlow
    private val sendMessageListener = Emitter.Listener {
        val jsonObject = JSONObject(it[0].toString())
       /* processMessage(
            response = jsonObject.toString(),
            stateFlow = _callSendMessageMutableStateFlow
        )*/
        Logger.e("sendMessageListener : $jsonObject")
    }

    /**
     * Send customer location, For Driver see customer location
     * */

    //Send customer location emit
    fun emitUpdateLocation(shipmentId: Int, lat: Double, lng: Double, driverId: Int) {
        val jsonObject = JSONObject().apply {
            put(KEY_SHIPMENT_ID, shipmentId)
            put(KEY_LATITUDE, lat)
            put(KEY_LONGITUDE, lng)
            put(KEY_DRIVER_ID, driverId)
        }
        Logger.e("emitUpdateLocation params: $jsonObject")
        mSocket?.emit(Events.EVENT_UPDATE_LOCATION.type, jsonObject, object : Ack {
            override fun call(vararg args: Any?) {
                // {status: 1,message: 'success'}
                val response = args[0].toString()
                Log.d(TAG, "${Events.EVENT_UPDATE_LOCATION.type}: response:$response")
            }
        })
        //here, no need to on listen
    }

   /* private fun processMessage(response: String, stateFlow: MutableStateFlow<Resource<*>>) {
        JSONObject(response).toString().toObject<ChatDetails>()?.let { data ->
            data.chatDateTime = data.createdAt?.getDateTimeFromUTCDateTime(
                inputFormat = TZ_ISO_FORMAT,
                outputFormat = h_mm_a
            ) ?: ""
            data.isMe = false
            val resource = Resource.Success(data.toBaseResponse())
            stateFlow.tryEmit(resource)
        }
    }

    private fun ChatDetails.toBaseResponse() = BaseResponse(
        status = true, message = "Success", data = ChatDetails(
            id = id,
            createdAt = createdAt,
            message = message,
            receiverId = receiverId,
            receiverType = receiverType,
            senderId = senderId,
            senderType = senderType,
            shipmentId = shipmentId,
            chatDateTime = chatDateTime,
            isMe = isMe
        )
    )*/
}