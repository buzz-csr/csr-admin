package com.naturalmotion.event;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import com.linecorp.bot.model.message.ImageMessage;
import com.linecorp.bot.model.message.ImageMessage.ImageMessageBuilder;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.TextMessage.Emoji;
import com.linecorp.bot.model.message.TextMessage.Emoji.EmojiBuilder;
import com.linecorp.bot.model.message.TextMessage.TextMessageBuilder;
import com.naturalmotion.database.TOKEN_RARITY;
import com.naturalmotion.webservice.service.json.Card;

public class MessageFactory {

	private SimpleDateFormat sdf = new SimpleDateFormat("EEEEE dd à HH:mm:ss", Locale.FRANCE);

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
		switch (tokenType) {
		case GOLD:
			message.append("$$$$");
			message.append(" $$$$$ $");

			// 150 %
			emojis.add(emoji(emojis.size(), "5ac21a8c040ab15980c9b43f", "053"));
			emojis.add(emoji(emojis.size(), "5ac21a8c040ab15980c9b43f", "057"));
			emojis.add(emoji(emojis.size(), "5ac21a8c040ab15980c9b43f", "062"));
			emojis.add(emoji(emojis.size(), "5ac21a8c040ab15980c9b43f", "071"));
			break;
		case SILVER:
			message.append("$$$");
			message.append(" $$$$$ $");

			// 70 %
			emojis.add(emoji(emojis.size(), "5ac21a8c040ab15980c9b43f", "059"));
			emojis.add(emoji(emojis.size(), "5ac21a8c040ab15980c9b43f", "062"));
			emojis.add(emoji(emojis.size(), "5ac21a8c040ab15980c9b43f", "071"));
			break;
		case BRONZE:
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
		emojis.add(emoji(emojis.size() + 1, "5ac21a8c040ab15980c9b43f", "042"));
		emojis.add(emoji(emojis.size() + 1, "5ac21a8c040ab15980c9b43f", "038"));
		emojis.add(emoji(emojis.size() + 1, "5ac21a8c040ab15980c9b43f", "031"));
		emojis.add(emoji(emojis.size() + 1, "5ac21a8c040ab15980c9b43f", "035"));
		emojis.add(emoji(emojis.size() + 1, "5ac21a8c040ab15980c9b43f", "040"));

		emojis.add(emoji(emojis.size() + 2, "5ac21a8c040ab15980c9b43f", "067"));
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
		URI uri = new URI("https://mod.csr-lesnains.fr/csr-admin/images/line/"
		        + TOKEN_RARITY.from(actualCard.getRarity()).getName() + "_" + crew + ".jpg");
		imBuilder.originalContentUrl(uri);
		imBuilder.previewImageUrl(uri);
		return imBuilder.build();
	}

	public TextMessage createUserTokenDonation(String playerName, String name, int paidDelta, Date date) {
		TextMessageBuilder tmBuilder = TextMessage.builder();
		StringBuilder message = new StringBuilder();
		message.append(sdf.format(date));
		message.append(" - ");
		message.append(playerName);
		message.append(" a posé ");
		message.append(paidDelta);
		message.append(" sur le ");
		message.append(name).append("%");
		tmBuilder.text(message.toString());
		return tmBuilder.build();
	}

	public TextMessage join(List<TextMessage> tMessages) {
		TextMessageBuilder tmBuilder = TextMessage.builder();
		StringBuilder message = new StringBuilder();
		Iterator<TextMessage> iterator = tMessages.iterator();
		TextMessage next = null;
		if (iterator.hasNext()) {
			next = iterator.next();
			message.append(next.getText());
		}
		while (iterator.hasNext()) {
			message.append("\n");
			message.append(iterator.next().getText());
		}
		tmBuilder.text(message.toString());
		return tmBuilder.build();
	}
}
