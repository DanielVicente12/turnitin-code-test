package integrations.turnitin.com.membersearcher.service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import integrations.turnitin.com.membersearcher.client.MembershipBackendClient;
import integrations.turnitin.com.membersearcher.model.MembershipList;

import integrations.turnitin.com.membersearcher.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MembershipService {
	@Autowired
	private MembershipBackendClient membershipBackendClient;

	/**
	 * Method to fetch all memberships with their associated user details included.
	 * This method calls out to the php-backend service and fetches all memberships,
	 * it then calls to fetch all user details and
	 * associates each of them with their corresponding membership.
	 *
	 * @return A CompletableFuture containing a fully populated MembershipList object.
	 */
	public CompletableFuture<MembershipList> fetchAllMembershipsWithUsers() {
		return membershipBackendClient.fetchMemberships()
				.thenCombine(membershipBackendClient.fetchUsers(), (members, users) -> {
					Map<String, User> usersMap = users.getUsers().stream().collect(Collectors.toMap(User::getId, user -> user));

					members.getMemberships().forEach(membership -> {
						membership.setUser(usersMap.get(membership.getUserId()));
					});
					return members;
				});
	}
}
