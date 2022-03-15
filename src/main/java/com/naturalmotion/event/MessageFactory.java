package com.naturalmotion.event;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.linecorp.bot.model.message.FlexMessage;
import com.linecorp.bot.model.message.ImageMessage;
import com.linecorp.bot.model.message.ImageMessage.ImageMessageBuilder;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.TextMessage.Emoji;
import com.linecorp.bot.model.message.TextMessage.Emoji.EmojiBuilder;
import com.linecorp.bot.model.message.TextMessage.TextMessageBuilder;
import com.linecorp.bot.model.message.flex.component.Box;
import com.linecorp.bot.model.message.flex.component.FlexComponent;
import com.linecorp.bot.model.message.flex.component.Image;
import com.linecorp.bot.model.message.flex.component.Image.ImageAspectMode;
import com.linecorp.bot.model.message.flex.component.Image.ImageSize;
import com.linecorp.bot.model.message.flex.component.Separator;
import com.linecorp.bot.model.message.flex.component.Text;
import com.linecorp.bot.model.message.flex.container.Bubble;
import com.linecorp.bot.model.message.flex.container.Bubble.BubbleSize;
import com.linecorp.bot.model.message.flex.container.BubbleStyles;
import com.linecorp.bot.model.message.flex.container.BubbleStyles.BlockStyle;
import com.linecorp.bot.model.message.flex.container.FlexContainer;
import com.linecorp.bot.model.message.flex.unit.FlexAlign;
import com.linecorp.bot.model.message.flex.unit.FlexLayout;
import com.linecorp.bot.model.message.flex.unit.FlexPaddingSize;
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

	public FlexMessage createFlexMessage(Map<String, List<List<String>>> data) throws URISyntaxException {
		FlexComponent heroComponent = Image.builder().size(ImageSize.FULL_WIDTH).aspectMode(ImageAspectMode.Cover)
		        .aspectRatio("10:2").url(new URI("https://mod.csr-lesnains.fr/csr-admin/images/line/Alliance1.png"))
		        .build();
		FlexContainer flexContainer = Bubble.builder()
		        .styles(BubbleStyles.builder()
		                .body(BlockStyle.builder().backgroundColor("#000000").separatorColor("#ffffff").build())
		                .build())
		        .hero(heroComponent).size(BubbleSize.GIGA).body(body(data)).build();
		return FlexMessage.builder().altText("Message du bot...").contents(flexContainer).build();
	}

	private Box body(Map<String, List<List<String>>> data) {
		List<FlexComponent> lines = new ArrayList<>();
		Iterator<String> keys = data.keySet().iterator();
		while (keys.hasNext()) {
			String day = keys.next();

			if (data.get(day).get(0).size() > 0) {
				lines.add(createLine(day, data.get(day)));
				if (keys.hasNext()) {
					lines.add(separator());
				}
			}
		}
		Box body = null;
		if (lines.isEmpty()) {
			body = Box.builder().width("100%").paddingAll(FlexPaddingSize.LG).layout(FlexLayout.HORIZONTAL)
			        .content(Text.builder().color("#ffffff").text("Aucun jeton n'a été posé").build()).build();
		} else {
			body = Box.builder().width("100%").paddingAll(FlexPaddingSize.LG).layout(FlexLayout.VERTICAL)
			        .contents(lines).build();
		}
		return body;
	}

	private Box createLine(String day, List<List<String>> list) {
		Text cell1 = Text.builder().flex(2).text(day).color("#ffffff").build();

		List<FlexComponent> userDetails = new ArrayList<>();
		for (int i = 0; i < list.get(0).size(); i++) {
			Text cellUserName = cellCenter(list.get(0).get(i));

			Box cellTokenDetails = Box.builder().layout(FlexLayout.HORIZONTAL)
			        .contents(cellCenter(list.get(1).get(i)), cellCenter(list.get(2).get(i))).build();

			userDetails.add(cellUserName);
			userDetails.add(cellTokenDetails);
			if (i < list.get(0).size() - 1) {
				userDetails.add(separator());
			}

		}
		Box cellUserDetails = Box.builder().flex(6).layout(FlexLayout.VERTICAL).contents(userDetails).build();
		return Box.builder().paddingTop(FlexPaddingSize.LG).layout(FlexLayout.HORIZONTAL)
		        .contents(cell1, separator(), cellUserDetails).build();
	}

	private Separator separator() {
		return Separator.builder().color("#ffffff").build();
	}

	private Text cellCenter(String name) {
		return Text.builder().align(FlexAlign.CENTER).text(name).color("#ffffff").wrap(true).build();
	}

}
