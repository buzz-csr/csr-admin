package com.naturalmotion.event;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import com.linecorp.bot.model.message.ImageMessage;
import com.linecorp.bot.model.message.ImageMessage.ImageMessageBuilder;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.TextMessage.Emoji;
import com.linecorp.bot.model.message.TextMessage.Emoji.EmojiBuilder;
import com.linecorp.bot.model.message.TextMessage.TextMessageBuilder;
import com.naturalmotion.database.TOKEN_RARITY;
import com.naturalmotion.webservice.service.json.Card;

public class MessageFactory {

	public TextMessage createGoldFull() {
		return create(TOKEN_RARITY.GOLD);
	}

	public TextMessage createSilverFull() {
		return create(TOKEN_RARITY.SILVER);
	}

	public TextMessage createBronzeFull() {
		return create(TOKEN_RARITY.BRONZE);
	}

	private TextMessage create(TOKEN_RARITY tokenType) {
		TextMessageBuilder tmBuilder = TextMessage.builder();
		StringBuilder message = new StringBuilder();
		List<Emoji> emojis = new ArrayList<TextMessage.Emoji>();

		// Joker
		addJoker(emojis);

		switch (tokenType) {
		case GOLD:
			message.append("$$$$$ ");
			message.append("$$$$");
			message.append(" $$$$$ $");

			// 150 %
			emojis.add(emoji(emojis.size(), "5ac21a8c040ab15980c9b43f", "053"));
			emojis.add(emoji(emojis.size(), "5ac21a8c040ab15980c9b43f", "057"));
			emojis.add(emoji(emojis.size(), "5ac21a8c040ab15980c9b43f", "062"));
			emojis.add(emoji(emojis.size(), "5ac21a8c040ab15980c9b43f", "071"));
			break;
		case SILVER:
			message.append("$$$$$ ");
			message.append("$$$");
			message.append(" $$$$$ $");

			// 70 %
			emojis.add(emoji(emojis.size(), "5ac21a8c040ab15980c9b43f", "059"));
			emojis.add(emoji(emojis.size(), "5ac21a8c040ab15980c9b43f", "062"));
			emojis.add(emoji(emojis.size(), "5ac21a8c040ab15980c9b43f", "071"));
			break;
		case BRONZE:
			message.append("$$$$$ ");
			message.append("$$$");
			message.append(" $$$$$ $");

			// 30 %
			emojis.add(emoji(emojis.size(), "5ac21a8c040ab15980c9b43f", "055"));
			emojis.add(emoji(emojis.size(), "5ac21a8c040ab15980c9b43f", "062"));
			emojis.add(emoji(emojis.size(), "5ac21a8c040ab15980c9b43f", "071"));
			break;

		default:
			break;
		}
		// Plein !
		addFull(emojis);

		tmBuilder.text(message.toString());
		tmBuilder.emojis(emojis);

		return tmBuilder.build();
	}

	private void addFull(List<Emoji> emojis) {
		emojis.add(emoji(emojis.size(), "5ac21a8c040ab15980c9b43f", "042"));
		emojis.add(emoji(emojis.size(), "5ac21a8c040ab15980c9b43f", "038"));
		emojis.add(emoji(emojis.size(), "5ac21a8c040ab15980c9b43f", "031"));
		emojis.add(emoji(emojis.size(), "5ac21a8c040ab15980c9b43f", "035"));
		emojis.add(emoji(emojis.size(), "5ac21a8c040ab15980c9b43f", "040"));
		emojis.add(emoji(emojis.size(), "5ac21a8c040ab15980c9b43f", "067"));
	}

	private void addJoker(List<Emoji> emojis) {
		emojis.add(emoji(emojis.size(), "5ac21a8c040ab15980c9b43f", "010"));
		emojis.add(emoji(emojis.size(), "5ac21a8c040ab15980c9b43f", "041"));
		emojis.add(emoji(emojis.size(), "5ac21a8c040ab15980c9b43f", "037"));
		emojis.add(emoji(emojis.size(), "5ac21a8c040ab15980c9b43f", "031"));
		emojis.add(emoji(emojis.size(), "5ac21a8c040ab15980c9b43f", "044"));
	}

	private Emoji emoji(int index, String productId, String emojiId) {
		EmojiBuilder builder = Emoji.builder();
		builder.productId(productId);
		builder.emojiId(emojiId);
		builder.index(index);
		return builder.build();
	}

	public ImageMessage createImage(Card actualCard, String crew) throws URISyntaxException {
		ImageMessageBuilder imBuilder = ImageMessage.builder();
		URI uri = new URI("https://mod.csr-lesnains.fr/csr-admin/images/line/" + String.valueOf(actualCard.getCost())
		        + "_" + crew + ".jpg");
		imBuilder.originalContentUrl(uri);
		imBuilder.previewImageUrl(uri);
		return imBuilder.build();
	}
}
